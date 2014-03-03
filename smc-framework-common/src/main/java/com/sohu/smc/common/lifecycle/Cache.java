package com.sohu.smc.common.lifecycle;



/**
 * 封装Resin的TimedCache类和SessionServer时间检查点功能
 * A timed LRU cache.  Items remain valid until they expire.
 * TimedCache can simplify database caching.
 *
 * @author Norton Chen(cjx)
 * @since  2006-12-11
 * @author Qinqidong
 * @since 2012-7-12 modify
 */
import com.caucho.util.Alarm;
import com.caucho.util.CacheListener;

public class Cache extends com.caucho.util.LruCache {
    SpyMemcachedFactory memcachedFactory = null;
    /**
     * Creates a new timed LRU cache.
     * @param capacity the maximum size of the LRU cache
     * @param expireInterval the time an entry remains valid in MillSeconds
     */
    public Cache(int capacity, long expireInterval) {
        this(capacity, expireInterval, false, null);
    }

    public Cache(int capacity, long expireInterval, boolean _needCheckpoint, String _moduleOfCheckpoint) {
        super(capacity);
        this.expireInterval = expireInterval;
        this.needCheckpoint = _needCheckpoint;
        this.moduleOfCheckpoint = _moduleOfCheckpoint;
        if(this.needCheckpoint){
            this.memcachedFactory = new SpyMemcachedFactory(_moduleOfCheckpoint);
        }
    }


    /**
     * Put a new item in the cache.
     */
    public Object put(Object key, Object value) {
        Object ret = super.put(key, new Entry(expireInterval, value));
        return ret;
    }

    /**
     * Gets an item from the cache, returning null if expired.
     */
    public Object get(Object key) {
        Entry entry = (Entry) super.get(key);
        if (entry == null)
            return null;

        if (! entry.isValid()) {
            super.remove(key);
            return null;
        } else if(needCheckpoint && !checkpoint(key, entry.checkTime)) { //需要严格检查点
            super.remove(key);
            return null;
        }
        return entry.getValue();
    }

    /**
     * 把Session Server 的一个节点"弄脏"，用于一个缓存节点有内存数据修改时，这样通知其他JVM缓存需作废
     */
    public void dirty(Object key) {
        if(key == null) return;
        if(needCheckpoint && moduleOfCheckpoint != null) { //需要严格检查点
            //检查点时间戳比当前少一点；过期时间比缓存本身多一点，消除服务器时钟同步问题
            if(memcachedFactory != null) memcachedFactory.set("cp_" + key, (int)(expireInterval/1000*1.1), System.currentTimeMillis() - 300);
        }
    }

    //-------------------------  private section -----------------------------
    private boolean checkpoint(Object key, long entryCreateTS) {
        if(key == null || entryCreateTS < 1000L) return true;
        //过期时间比缓存本身多5分钟，消除服务器时钟同步问题
        if(memcachedFactory == null) return true;
        long checkpoint = memcachedFactory.getLong("cp_" + key);
        if(checkpoint > entryCreateTS) {
            System.out.println("INFO: CRCache.checkpoint return flase! key=" + key + " entryCreateTS=" + entryCreateTS + " checkpoint=" + checkpoint);
            return false;
        }
        //System.out.println("DEBUG: CRCache.checkpoint return true! key=" + key + " entryCreateTS=" + entryCreateTS + " checkpoint=" + checkpoint);
        return true;
    }

    private boolean needCheckpoint;
    private String moduleOfCheckpoint;
    private long expireInterval;
    /**
     * 内部类：缓存节点封装
     * Class representing a cached entry.
     */
    static class Entry implements CacheListener {
        long expireInterval;
        long checkTime;
        Object value;

        Entry(long expireInterval, Object value) {
            this.expireInterval = expireInterval;
            this.value = value;
            checkTime = Alarm.getCurrentTime();
        }

        boolean isValid() {
            return Alarm.getCurrentTime() < checkTime + expireInterval;
        }

        Object getValue() {
            return value;
        }

        public void removeEvent() {
            if (value instanceof CacheListener)
                ((CacheListener) value).removeEvent();
        }
    }
}
