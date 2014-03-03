package com.sohu.smc.core.http;

import com.stumbleupon.async.Deferred;
import com.twitter.ostrich.stats.Stats;
import org.jboss.netty.channel.Channel;

public abstract class TelnetAction {

    /**
     * Executes this RPC.
     *
     * @param chan    The channel on which the RPC was received.
     * @param command The command received, split.
     * @return A deferred result.
     */
    public abstract Deferred<Object> execute(Channel chan, String[] command);

    public void addCounter(String key, int count) {
        Stats.incr(key, count);
    }

    public void addCounter(String key) {
        Stats.incr(key);
    }

    public void setLabel(String key, String value) {
        Stats.setLabel(key, value);
    }
}
