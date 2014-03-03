package com.sohu.smc.core.cli;

import com.sohu.smc.core.AbstractService;
import com.sohu.smc.core.config.Configuration;
import com.sohu.smc.core.config.Environment;
import com.sohu.smc.core.config.ServerFactory;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs a service as an HTTP server.
 *
 * @param <T> the {@link com.sohu.smc.core.config.Configuration} subclass which is loaded from the configuration file
 */
public class ServerCommand<T extends Configuration> extends ConfiguredCommand<T> {
    private final Logger log = LoggerFactory.getLogger(ServerCommand.class.getName());

    private final Class<T> configurationClass;

    /**
     * Creates a new {@link com.sohu.smc.core.cli.ServerCommand} with the given configuration class.
     *
     * @param configurationClass the configuration class the YAML file is parsed as
     */
    public ServerCommand(Class<T> configurationClass) {
        super("server", "Successfully starts an HTTP server running the service.");
        this.configurationClass = configurationClass;
    }

    /*
     * Since we don't subclass ServerCommand, we need a concrete reference to the configuration
     * class.
     */
    @Override
    protected Class<T> getConfigurationClass() {
        return configurationClass;
    }

    @Override
    protected void run(AbstractService<T> service, T configuration, CommandLine params) throws Exception {

        final Environment environment = new Environment(configuration, service);
        service.initializeWithBundles(configuration, environment);
        ServerFactory server = new ServerFactory(configuration, environment);
        //regist system action. start|stop|suspect
        //environment.addAction(new SystemAction(server));
        UsagePrinter.printHttpServerStarting(this, configuration, environment);

        server.start(environment);
    }
}
