package com.sohu.smc.helloworld.cli;

import com.google.common.base.Optional;
import com.sohu.smc.core.AbstractService;
import com.sohu.smc.core.cli.ConfiguredCommand;
import com.sohu.smc.core.template.Template;
import com.sohu.smc.helloworld.HelloWorldConfiguration;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderCommand extends ConfiguredCommand<HelloWorldConfiguration> {
    private static final Logger LOG = LoggerFactory.getLogger(RenderCommand.class.getName());

    public RenderCommand() {
        super("render", "Renders the configured template to the console.");
    }

    @Override
    protected String getConfiguredSyntax() {
        return "[name1 name2]";
    }

    @Override
    public Options getOptions() {
        final Options options = new Options();
        options.addOption("i", "include-default", false,
                "Also render the template with the default name");
        return options;
    }

    @Override
    protected void run(AbstractService<HelloWorldConfiguration> service,
                       HelloWorldConfiguration configuration,
                       CommandLine params) throws Exception {
        final Template template = configuration.buildTemplate();

        if (params.hasOption("include-default")) {
            LOG.info("DEFAULT => {}", template.render(Optional.<String>absent()));
        }

        for (String name : params.getArgs()) {
            LOG.info("{} => {}", name, template.render(Optional.of(name)));
        }
    }
}
