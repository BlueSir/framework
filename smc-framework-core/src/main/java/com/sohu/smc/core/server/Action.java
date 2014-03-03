package com.sohu.smc.core.server;


import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 * User: zhangsuozhu
 * Date: 13-1-14
 * Time: 下午5:31
 */
public abstract class Action {
    private final String name;
    //  private Object controller;
    private byte method = 0;

    public Action(String url) {
        this.name = url;
    }


    public abstract String execute(HttpRequest req, HttpResponse resp);


    public String getName() {
        return name;
    }


    public void setMethod(byte method) {
        this.method = method;
    }

    public byte getMethod() {
        return method;
    }
}
