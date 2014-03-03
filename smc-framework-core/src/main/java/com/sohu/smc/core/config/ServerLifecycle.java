package com.sohu.smc.core.config;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 12-9-4
 * Time: 下午5:38
 * To change this template use File | Settings | File Templates.
 */
public interface ServerLifecycle {

    void start(Environment env);

    void stop();

//    void suspend();
}
