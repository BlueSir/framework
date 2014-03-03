package com.sohu.smc.core.server.util;

import com.sohu.smc.core.annotation.RequestMethod;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * User: zhangsuozhu
 * Date: 13-1-17
 * Time: 下午3:37
 */
public final class ServerUtil {
    private static final Logger log = LoggerFactory.getLogger(ServerUtil.class);
    private static final Map<String, Integer> methodMap;



    static {
        methodMap = new HashMap<String, Integer>(RequestMethod.values().length);
        for (RequestMethod rm : RequestMethod.values()) {
            methodMap.put(rm.name(), rm.getValue());
        }
    }

    public static final boolean isMethodAllowed(HttpMethod httpMethod, byte method) {
        if (method == 0) {
            return true;
        }

        int m = methodMap.get(httpMethod.getName());
        if ((m & method) > 0) {
            return true;
        }
        return false;

    }
}
