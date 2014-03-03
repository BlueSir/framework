package com.sohu.smc.simpledb;

import com.sohu.smc.core.Service;
import com.sohu.smc.core.config.Environment;

/**
 * Created with IntelliJ IDEA.
 * User: huixiao200068
 * Date: 13-1-15
 * Time: 下午3:05
 * To change this template use File | Settings | File Templates.
 */
public class SimpleDBService extends Service<SimpleDBConfigure> {
    public static void main(String... args)  throws Exception {
        new SimpleDBService().run(args);
    }

    @Override
    protected void initialize(SimpleDBConfigure configuration, Environment environment) throws Exception {
//        environment.uri("/querylog", new QueryLogAction()).action("queryLog", HttpMethod.GET);
//        environment.uri("/test", new QueryLogAction()).action("test", HttpMethod.GET);
    }
}
