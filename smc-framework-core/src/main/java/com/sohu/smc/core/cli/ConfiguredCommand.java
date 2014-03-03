package com.sohu.smc.core.cli;

import com.sohu.smc.core.AbstractService;
import com.sohu.smc.core.config.Configuration;
import com.sohu.smc.core.config.ConfigurationException;
import com.sohu.smc.core.config.ConfigurationFactory;
import com.sohu.smc.core.validation.Validator;
import org.apache.commons.cli.CommandLine;

import java.lang.reflect.ParameterizedType;

/**
 * A command whose first parameter is the location of a YAML configuration file. That file is parsed
 *
 * @param <T> the {@link com.sohu.smc.core.config.Configuration} subclass which is loaded from the configuration file
 * @see com.sohu.smc.core.config.Configuration
 */
public abstract class ConfiguredCommand<T extends Configuration> extends Command {
    /**
     * Creates a new {@link com.sohu.smc.core.cli.ConfiguredCommand} with the given name and configuration.
     *
     * @param name        the command's name
     * @param description a description of the command
     */
    protected ConfiguredCommand(String name, String description) {
        super(name, description);
    }

    @SuppressWarnings("unchecked")
    protected Class<T> getConfigurationClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Returns the usage syntax, minus the configuration file param.
     *
     * @return the command's usage syntax
     */
    protected String getConfiguredSyntax() {
        return null;
    }

    @Override
    protected final String getSyntax() {
        final StringBuilder syntax = new StringBuilder("<conf file>");
        final String configured = getConfiguredSyntax();
        if ((configured != null) && !configured.isEmpty()) {
            syntax.append(' ').append(configured);
        }
        return syntax.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected final void run(AbstractService<?> service, CommandLine params) throws Exception {
        final ConfigurationFactory<T> factory = ConfigurationFactory.forClass(getConfigurationClass(), new Validator(), service.getJacksonModules());
        final String[] args = params.getArgs();
        if (args.length >= 1) {
            params.getArgList().remove(0);
            try {
                final T configuration = factory.build(args);
//                new LoggingFactory(configuration.getLoggingConfiguration()).configure();
                run((AbstractService<T>) service, configuration, params);
            } catch (ConfigurationException e) {
                e.printStackTrace();
                printHelp(e.getMessage(), service.getClass());
            }
        } else {
            printHelp(service.getClass());
            System.exit(-1);
        }
    }

    /**
     * Runs the command with the given {@link com.sohu.smc.core.AbstractService} and {@link com.sohu.smc.core.config.Configuration}.
     *
     * @param service       the service to which the command belongs
     * @param configuration the configuration object
     * @param params        any additional command-line parameters
     * @throws Exception if something goes wrong
     */
    protected abstract void run(AbstractService<T> service,
                                T configuration,
                                CommandLine params) throws Exception;
}
