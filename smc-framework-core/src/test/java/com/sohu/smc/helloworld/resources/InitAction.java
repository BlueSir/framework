package com.sohu.smc.helloworld.resources;

import com.sohu.smc.core.http.Action;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.util.HashMap;

public class InitAction extends Action {

    public InitAction() {
        super("init");
    }

    public String action(HttpRequest request, HttpResponse response) throws Exception {
        addCounter("updateIcon");
        HashMap map = packRequest(request);

        return "OK";
    }
}
