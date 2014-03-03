package com.sohu.smc.litecore;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: qinqd
 * Date: 11-10-18
 * Time: 下午4:37
 * To change this template use File | Settings | File Templates.
 */
public class OAction extends Action{
    @Override
    protected String action(HttpRequest req, HttpResponse resp) throws IOException {
        return "aa---------------bb";
    }
}
