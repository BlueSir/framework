package com.sohu.smc.litecore;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Controller {

    private final String name;
    protected final Map<String, Action> actions = new HashMap<String, Action>();

    public Controller(String name) {
        this.name = name;
    }

    public final String doAction(HttpRequest request, HttpResponse response) throws Exception {
        String uri = request.getUri();
        if(uri.indexOf('?')>0) uri = uri.substring(0,uri.indexOf('?'));
        String[] uriSplits = uri.split("/");
        if (uriSplits.length < 3) {
            invalidRequestFormat(request, response);
            return "";
        }
        if (!name.equals(uriSplits[1])) {
            invalidRequestFormat(request, response);
            return "";
        }

        String actionStr = uriSplits[2];
        Action action = actions.get(actionStr);
        if (action == null) {
            unknownAction(actionStr, response);
            return "";
        } else {
            //response.setStatus(HttpServletResponse.SC_OK);
            return  action.action(request, response);
        }
    }

    private final void invalidRequestFormat(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatus(HttpResponseStatus.valueOf(400));
        response.setContent(ChannelBuffers.copiedBuffer("Incorrectly formatted request: " + request.getUri(), CharsetUtil.UTF_8));
    }

    private final void unknownAction(String action, HttpResponse response) throws IOException {
        response.setStatus(HttpResponseStatus.valueOf(404));
        response.setContent(ChannelBuffers.copiedBuffer("Unknown action: " + action, CharsetUtil.UTF_8));
    }
}
