package com.sohu.smc.core.bundles;

import com.sohu.smc.core.config.Environment;

/**
 * A reusable bundle of functionality, used to define blocks of service behavior that are
 * conditional on configuration parameters.
 *
 * @param <T>    the required configuration interface
 */
public interface ConfiguredBundle<T> {
    /**
     * Initializes the environment.
     *
     * @param configuration    the configuration object
     * @param environment      the service's {@link com.sohu.smc.core.config.Environment}
     */
    public void initialize(T configuration, Environment environment);
}
