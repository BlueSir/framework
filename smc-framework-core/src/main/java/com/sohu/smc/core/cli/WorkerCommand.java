package com.sohu.smc.core.cli;

import com.sohu.smc.core.AbstractService;
import com.sohu.smc.core.config.Configuration;
import org.apache.commons.cli.CommandLine;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 12-9-11
 * Time: 下午5:22
 * To change this template use File | Settings | File Templates.
 */
public class WorkerCommand<T extends Configuration> extends ConfiguredCommand<T> {
    /**
     * Creates a new {@link ConfiguredCommand} with the given name and configuration.
     *
     * @param name        the command's name
     * @param description a description of the command
     */
    protected WorkerCommand(String name, String description) {
        super(name, description);
    }

    @Override
    protected void run(AbstractService<T> service, T configuration, CommandLine params) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
