package com.sohu.smc.core.http;

import com.twitter.ostrich.stats.Stats;

public abstract class MemcachedAction {
    public abstract boolean set(String key, int flags, int expiry, byte[] data) throws Exception;

    public abstract byte[] get(String key) throws Exception;

    public abstract void delete(String key) throws Exception;

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
