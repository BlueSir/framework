package com.sohu.smc.core.bundles;

import com.sohu.smc.core.Service;
import com.sohu.smc.core.admin.LifeCycleStat;
import com.sohu.smc.core.config.Environment;

/**
 * Initializes the service with support for Java classes.
 */
public class JavaBundle implements Bundle {
    //public static final ImmutableList<Object> DEFAULT_PROVIDERS = ImmutableList.<Object>of(
    //        new OptionalQueryParamInjectableProvider()
    //);

    private LifeCycleStat stat;

    private final Service<?> service;

    public JavaBundle(Service<?> service) {
        this.service = service;
    }

    @Override
    public void initialize(Environment environment) {
        stat = LifeCycleStat.INIT;
        /*environment.addProvider(new JacksonMessageBodyProvider(service.getJson()));
        for (Object provider : DEFAULT_PROVIDERS) {
            environment.addProvider(provider);
        } */
    }
}
