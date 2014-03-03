package com.sohu.smc.core.config;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sohu.smc.common.lifecycle.ExecutorServiceManager;
import com.sohu.smc.common.lifecycle.Managed;
import com.sohu.smc.core.AbstractService;
import com.sohu.smc.core.admin.AbstractLifeCycle;
import com.sohu.smc.core.server.Controller;
import com.sohu.smc.core.tasks.Task;
import com.twitter.finagle.SimpleFilter;
import com.yammer.metrics.core.HealthCheck;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EventListener;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * A Dropwizard service's environment.
 * <p/>
 * REVIEW: 11/12/11 <coda> -- Probably better to invert this code.
 * Instead of letting it collect intermediate results and then exposing those via package-private
 * getters, it might be better to pass this a ServletContextHandler, etc., and have it modify
 * those directly. That's easier to test.
 */
public class Environment extends AbstractLifeCycle {
    private static final Logger log = LoggerFactory.getLogger(Environment.class);

    private final AbstractService<?> service;
    private final Controller controller;
    private final ImmutableSet.Builder<HealthCheck> healthChecks;
    private final ImmutableSet.Builder<EventListener> servletListeners;
    private final CopyOnWriteArrayList<SimpleFilter<HttpRequest, HttpResponse>> filter_chain = new CopyOnWriteArrayList<SimpleFilter<HttpRequest, HttpResponse>>();


    public Controller getController() {
        return controller;
    }


    private final ImmutableSet.Builder<Task> tasks;

    /**
     * Creates a new environment.
     *
     * @param configuration the service's {@link Configuration}
     * @param service       the service
     */
    public Environment(Configuration configuration, AbstractService<?> service) {
        this.service = service;
        this.healthChecks = ImmutableSet.builder();
        this.servletListeners = ImmutableSet.builder();
        this.tasks = ImmutableSet.builder();
//        this.lifeCycle = new AggregateLifeCycle();
        this.controller = new Controller(configuration.getHttpConfiguration().getServiceName());
        //addTask(new GarbageCollectionTask());
    }



    public void addFilter(SimpleFilter<HttpRequest, HttpResponse> filter) {
        if (filter_chain.size() > 10) {
            log.error("max filter num is 10. ");
            return;
        }

        filter_chain.add(filter);
    }

    public CopyOnWriteArrayList<SimpleFilter<HttpRequest, HttpResponse>> getFilters() {
        return filter_chain;
    }


    /**
     * Adds the given health check to the set of health checks exposed on the admin port.
     *
     * @param healthCheck a health check
     */
    public void addHealthCheck(HealthCheck healthCheck) {
        healthChecks.add(checkNotNull(healthCheck));
    }

    /**
     * Adds the given {@link com.sohu.smc.common.lifecycle.Managed} instance to the set of objects managed by the server's
     * lifecycle. When the server starts, {@code managed} will be started. When the server stops,
     * {@code managed} will be stopped.
     *
     * @param managed a managed object
     */
    public void manage(Managed managed) {
//        lifeCycle.addBean(new JettyManaged(checkNotNull(managed)));
    }

    /**
     * Adds a {@link com.sohu.smc.core.tasks.Task} instance.
     *
     * @param task a {@link com.sohu.smc.core.tasks.Task}
     */
    public void addTask(Task task) {
        tasks.add(checkNotNull(task));
    }

    /**
     * Creates a new {@link java.util.concurrent.ExecutorService} instance with the given parameters whose lifecycle is
     * managed by the service.
     *
     * @param nameFormat      a {@link String#format(String, Object...)}-compatible format
     *                        String, to which a unique integer (0, 1, etc.) will be
     *                        supplied as the single parameter.
     * @param corePoolSize    the number of threads to keep in the pool, even if they are
     *                        idle.
     * @param maximumPoolSize the maximum number of threads to allow in the pool.
     * @param keepAliveTime   when the number of threads is greater than the core, this is
     *                        the maximum time that excess idle threads will wait for new
     *                        tasks before terminating.
     * @param unit            the time unit for the keepAliveTime argument.
     * @return a new {@link java.util.concurrent.ExecutorService} instance
     */
    public ExecutorService managedExecutorService(String nameFormat,
                                                  int corePoolSize,
                                                  int maximumPoolSize,
                                                  long keepAliveTime,
                                                  TimeUnit unit) {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(nameFormat)
                .build();
        final ExecutorService executor = new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                new LinkedBlockingQueue<Runnable>(),
                threadFactory);
        manage(new ExecutorServiceManager(executor, 5, TimeUnit.SECONDS));
        return executor;
    }

    /**
     * Creates a new {@link java.util.concurrent.ScheduledExecutorService} instance with the given parameters whose
     * lifecycle is managed by the service.
     *
     * @param nameFormat   a {@link String#format(String, Object...)}-compatible format
     *                     String, to which a unique integer (0, 1, etc.) will be
     *                     supplied as the single parameter.
     * @param corePoolSize the number of threads to keep in the pool, even if they are
     *                     idle.
     * @return a new {@link java.util.concurrent.ScheduledExecutorService} instance
     */
    public ScheduledExecutorService managedScheduledExecutorService(String nameFormat,
                                                                    int corePoolSize) {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(nameFormat)
                .build();
        final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(corePoolSize,
                threadFactory);
        manage(new ExecutorServiceManager(executor, 5, TimeUnit.SECONDS));
        return executor;
    }

    /*
     * Internal Accessors
     */

    ImmutableSet<HealthCheck> getHealthChecks() {
        return healthChecks.build();
    }

    ImmutableSet<Task> getTasks() {
        return tasks.build();
    }

    ImmutableSet<EventListener> getServletListeners() {
        return servletListeners.build();
    }

    private void logHealthChecks() {
        final ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (HealthCheck healthCheck : healthChecks.build()) {
            builder.add(healthCheck.getClass().getCanonicalName());
        }
        log.debug("health checks = {}", builder.build());
    }



    public AbstractService<?> getService() {
        return service;
    }
}
