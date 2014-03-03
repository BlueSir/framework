package com.sohu.smc.helloworld;

import com.sohu.smc.core.config.Configuration;
import com.sohu.smc.core.template.Template;

@SuppressWarnings("FieldMayBeFinal")
public class HelloWorldConfiguration extends Configuration {
    private String template;
    
    private String defaultName = "Stranger";

    public String getTemplate() {
        return template;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public Template buildTemplate() {
        return new Template(template, defaultName);
    }
}
