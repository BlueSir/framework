package com.sohu.smc.core.server.builer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * User: zhangsuozhu
 * Date: 13-1-17
 * Time: 下午3:18
 */
public class SpringProxyFactory implements ProxyFactory {
    private static final Logger log = LoggerFactory.getLogger(SpringProxyFactory.class);
    private final ApplicationContext applicationContext;

    public SpringProxyFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T> T getObject(Class<T> tClass) {
        return this.applicationContext.getBean(tClass);
    }
}
