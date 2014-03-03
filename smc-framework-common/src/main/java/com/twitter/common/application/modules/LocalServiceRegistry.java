// =================================================================================================
// Copyright 2011 Twitter, Inc.
// -------------------------------------------------------------------------------------------------
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this work except in compliance with the License.
// You may obtain a copy of the License in the LICENSE file, or at:
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// =================================================================================================

package com.twitter.common.application.modules;

import com.google.common.base.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.twitter.common.application.ShutdownRegistry;
import com.twitter.common.application.modules.LifecycleModule.LaunchException;
import com.twitter.common.application.modules.LifecycleModule.Service;
import com.twitter.common.application.modules.LifecycleModule.ServiceRunner;
import com.twitter.common.base.Command;
import com.twitter.common.net.InetSocketAddressHelper;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Registry for services that should be exported from the application.
 *
 * Example of announcing and registering a port:
 * <pre>
 * class MyLauncher implements Provider<LocalService> {
 *   public LocalService get() {
 *     // Launch service.
 *   }
 * }
 *
 * class MyServiceModule extends AbstractModule {
 *   public void configure() {
 *     LifeCycleModule.bindServiceLauncher(binder(), MyLauncher.class);
 *   }
 * }
 * </pre>
 *
 * @author William Farner
 */
public class LocalServiceRegistry {

  private static final Predicate<LocalService> IS_PRIMARY = new Predicate<LocalService>() {
    @Override public boolean apply(LocalService service) {
      return service.primary;
    }
  };

  private static final Function<LocalService, InetSocketAddress> SERVICE_TO_SOCKET =
      new Function<LocalService, InetSocketAddress>() {
        @Override public InetSocketAddress apply(LocalService service) {
          try {
            return InetSocketAddressHelper.getLocalAddress(service.port);
          } catch (UnknownHostException e) {
            throw new RuntimeException("Failed to resolve local address for " + service, e);
          }
        }
      };

  private static final Function<LocalService, String> GET_NAME =
      new Function<LocalService, String>() {
        @Override public String apply(LocalService service) {
          return service.name.get();
        }
      };

  private final ShutdownRegistry shutdownRegistry;
  private final Provider<Set<ServiceRunner>> runnerProvider;

  private Optional<InetSocketAddress> primarySocket = null;
  private Map<String, InetSocketAddress> auxiliarySockets = null;

  /**
   * Creates a new local service registry.
   *
   * @param runnerProvider provider of registered local services.
   * @param shutdownRegistry Shutdown registry to tear down launched services.
   */
  @Inject
  public LocalServiceRegistry(@Service Provider<Set<ServiceRunner>> runnerProvider,
      ShutdownRegistry shutdownRegistry) {
    this.runnerProvider = Preconditions.checkNotNull(runnerProvider);
    this.shutdownRegistry = Preconditions.checkNotNull(shutdownRegistry);
  }

  /**
   * Launches the local services if not already launched, otherwise this is a no-op.
   */
  void ensureLaunched() {
    if (primarySocket == null) {
      ImmutableList.Builder<LocalService> builder = ImmutableList.builder();

      for (ServiceRunner runner : runnerProvider.get()) {
        try {
          LocalService service = runner.launch();
          builder.add(service);
          shutdownRegistry.addAction(service.shutdownCommand);
        } catch (LaunchException e) {
          throw new IllegalStateException("Failed to launch " + runner, e);
        }
      }

      List<LocalService> localServices = builder.build();
      Iterable<LocalService> primaries = Iterables.filter(localServices, IS_PRIMARY);
      switch (Iterables.size(primaries)) {
        case 0:
          primarySocket = Optional.absent();
          break;

        case 1:
          primarySocket = Optional.of(SERVICE_TO_SOCKET.apply(Iterables.getOnlyElement(primaries)));
          break;

        default:
          throw new IllegalArgumentException("More than one primary local service: " + primaries);
      }

      Map<String, LocalService> byName;
      try {
        byName = Maps.uniqueIndex(
            Iterables.filter(localServices, Predicates.not(IS_PRIMARY)), GET_NAME);
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Auxiliary services with identical names.", e);
      }

      auxiliarySockets = ImmutableMap.copyOf(Maps.transformValues(byName, SERVICE_TO_SOCKET));
    }
  }

  /**
   * Gets the mapping from auxiliary port name to socket.
   *
   * @return Auxiliary port mapping.
   */
  public synchronized Map<String, InetSocketAddress> getAuxiliarySockets() {
    ensureLaunched();
    return auxiliarySockets;
  }

  /**
   * Gets the optional primary socket address, and returns an unresolved local socket address
   * representing that port.
   *
   * @return Local socket address for the primary port.
   * @throws IllegalStateException If the primary port was not set.
   */
  public synchronized Optional<InetSocketAddress> getPrimarySocket() {
    ensureLaunched();
    return primarySocket;
  }

  /**
   * An individual local service.
   */
  public static final class LocalService {
    private final boolean primary;
    private final Optional<String> name;
    private final int port;
    private final Command shutdownCommand;

    private LocalService(boolean primary, Optional<String> name, int port,
        Command shutdownCommand) {
      this.primary = primary;
      this.name = name;
      this.port = port;
      this.shutdownCommand = shutdownCommand;
    }

    @Override
    public String toString() {
      return new ToStringBuilder(this)
          .append("primary", primary)
          .append("name", name)
          .append("port", port)
          .toString();
    }

    /**
     * Creates a primary local service.
     *
     * @param port Service port.
     * @param shutdownCommand A command that will shut down the service.
     * @return A new primary local service.
     */
    public static LocalService primaryService(int port, Command shutdownCommand) {
      return new LocalService(true, Optional.<String>absent(), port, shutdownCommand);
    }

    /**
     * Creates a named auxiliary service.
     *
     * @param name Service name.
     * @param port Service port.
     * @param shutdownCommand A command that will shut down the service.
     * @return A new auxiliary local service.
     */
    public static LocalService auxiliaryService(String name, int port, Command shutdownCommand) {
      return new LocalService(false, Optional.of(name), port, shutdownCommand);
    }
  }
}
