package com.twitter.common.logging;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-1-22
 * Time: 下午11:44
 * To change this template use File | Settings | File Templates.
 */
public interface CacheCallback<T> {
    /**
     * return the object to the Object Pool
     *
     * @param log
     */
    public void returnObject(T log);

    /**
     * flush the object list cache, write to the scribe server
     *
     * @param list
     */
    public void flushCache(List<T> list);
}
