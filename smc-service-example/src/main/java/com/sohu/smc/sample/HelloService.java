package com.sohu.smc.sample;

import com.sohu.smc.common.util.SystemParam;
import com.sohu.smc.core.Service;
import com.sohu.smc.core.config.Environment;
import com.sohu.smc.core.metric.HystrixMetricsTask;
import com.sohu.smc.core.metric.MetricTaskManager;
import com.sohu.smc.core.metric.OstrichMetricTask;
import com.sohu.smc.core.template.Template;
import com.sohu.smc.core.template.TemplateHealthCheck;
import com.sohu.smc.sample.resource.HelloConfigure;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 12-12-18
 * Time: 下午3:11
 * To change this template use File | Settings | File Templates.
 */
public class HelloService extends Service<HelloConfigure> {

    public static void main(String... args) throws Exception {
        new HelloService().run(args);
    }

    @Override
    protected void initialize(HelloConfigure configuration, Environment environment) throws Exception {
        final Template template = configuration.buildTemplate();
        environment.addHealthCheck(new TemplateHealthCheck(template));

        MetricTaskManager manager = new MetricTaskManager();
        String intance = SystemParam.getInstanceName(), ip = configuration.getHttpConfiguration().getIp();
        int port = configuration.getHttpConfiguration().getPort();
        manager.addTask(new HystrixMetricsTask(intance, ip, port));
        manager.addTask(new OstrichMetricTask(intance, ip, port));
    }
}
