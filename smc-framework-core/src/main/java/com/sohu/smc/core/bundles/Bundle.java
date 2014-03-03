package com.sohu.smc.core.bundles;

import com.sohu.smc.core.config.Environment;

/**
 * 生命周期的定义
 */
public interface Bundle {
    /**
     * Initializes the environment.
     *
     * @param environment the service's {@link com.sohu.smc.core.config.Environment}
     */
    void initialize(Environment environment);
}
