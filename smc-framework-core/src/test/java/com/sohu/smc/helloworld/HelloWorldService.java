package com.sohu.smc.helloworld;

import com.sohu.smc.core.Service;
import com.sohu.smc.core.bundles.AssetsBundle;
import com.sohu.smc.core.config.Environment;
import com.sohu.smc.core.template.Template;
import com.sohu.smc.helloworld.cli.RenderCommand;

public class HelloWorldService extends Service<HelloWorldConfiguration> {
    public static void main(String[] args) throws Exception {
        new HelloWorldService().run(args);
    }

    private HelloWorldService() {
        addCommand(new RenderCommand());
        addBundle(new AssetsBundle());
    }

    @Override
    protected void initialize(HelloWorldConfiguration configuration,
                              Environment environment) {
        final Template template = configuration.buildTemplate();


        //environment.addHealthCheck(new TemplateHealthCheck(template));
//        environment.uri("/blogs.{format}", new BlogController())
//                .action("readAll", HttpMethod.GET);
                //.method(HttpMethod.POST);
    }

}
