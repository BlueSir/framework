package com.sohu.smc.core.server.builer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: zhangsuozhu
 * Date: 13-1-17
 * Time: 下午3:17
 */
public class DefaultProxyFactory implements ProxyFactory {
    private static final Logger log = LoggerFactory.getLogger(DefaultProxyFactory.class);

    @Override
    public <T> T getObject(Class<T> tClass) {
        try {
            return tClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
