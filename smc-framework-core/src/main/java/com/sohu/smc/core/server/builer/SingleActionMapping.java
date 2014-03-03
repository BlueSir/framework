package com.sohu.smc.core.server.builer;

import com.sohu.smc.core.server.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * User: zhangsuozhu
 * Date: 13-1-15
 * Time: 下午6:06
 */
public final class SingleActionMapping {
    private static final Logger log = LoggerFactory.getLogger(SingleActionMapping.class);
    private final Map<String, Action> mapping = new HashMap<String, Action>();
    private static final SingleActionMapping instance = new SingleActionMapping();

    public final static SingleActionMapping getInstance() {
        return instance;
    }

    public final void addAction(String url, Action action) {
        mapping.put(url, action);
    }

    public final Action getAction(String uri) {
        return mapping.get(uri);
    }

    public final Map<String, Action> getMapping() {
        return mapping;
    }
}
