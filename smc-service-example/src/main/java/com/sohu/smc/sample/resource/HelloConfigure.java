package com.sohu.smc.sample.resource;

import com.sohu.smc.core.config.Configuration;
import com.sohu.smc.core.template.Template;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 12-12-18
 * Time: 下午3:12
 * To change this template use File | Settings | File Templates.
 */
public class HelloConfigure extends Configuration {
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
