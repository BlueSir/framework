package com.sohu.smc.core.server;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.sohu.smc.core.server.builer.SingleActionMapping;

/**
 * User: zhangsuozhu
 * Date: 13-1-17
 * Time: 上午11:30
 */
public final class ActionCommand extends HystrixCommand<Action> {

    private final String url;

    public ActionCommand(String url) {
        super(HystrixCommandGroupKey.Factory.asKey(url));
        this.url = url;
    }

    @Override
    protected final Action run() {
        return SingleActionMapping.getInstance().getAction(url);
    }
}
