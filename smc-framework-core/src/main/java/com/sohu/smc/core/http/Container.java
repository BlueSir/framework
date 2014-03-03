package com.sohu.smc.core.http;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: Qinqd
 * Date: 12-9-19
 * Time: 下午2:46
 * To change this template use File | Settings | File Templates.
 */
public class Container {
    public final static ConcurrentMap<String, Action> container = new ConcurrentHashMap<String, Action>();
}
