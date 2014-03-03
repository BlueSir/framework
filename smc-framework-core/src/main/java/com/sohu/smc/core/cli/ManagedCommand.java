package com.sohu.smc.core.cli;

import com.sohu.smc.core.AbstractService;
import com.sohu.smc.core.config.Configuration;
import com.sohu.smc.core.config.Environment;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ManagedCommand<T extends Configuration> extends ConfiguredCommand<T> {
    private final Logger log = LoggerFactory.getLogger(ManagedCommand.class.getName());

    protected ManagedCommand(String name,
                             String description) {
        super(name, description);
    }

    @Override
    protected final void run(AbstractService<T> service,
                             T configuration,
                             CommandLine params) throws Exception {
//        new LoggingFactory(configuration.getLoggingConfiguration()).configure();
        final Environment environment = new Environment(configuration, service);
        service.initializeWithBundles(configuration, environment);
        String serviceName = configuration.getHttpConfiguration().getServiceName();
        log.info("Starting {}", serviceName);

    }

    protected abstract void run(T configuration,
                                Environment environment,
                                CommandLine params) throws Exception;
}
