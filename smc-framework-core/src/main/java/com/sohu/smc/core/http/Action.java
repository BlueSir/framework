package com.sohu.smc.core.http;

import com.twitter.finagle.Service;
import com.twitter.ostrich.stats.Stats;
import com.twitter.util.Future;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.ACCEPT_CHARSET;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 标准的请求响应
 * User: shijinkui
 * Date: 12-9-18
 * Time: 下午11:03
 * To change this template use File | Settings | File Templates.
 */
public abstract class Action extends Service<HttpRequest, HttpResponse> {

    private final static Logger logger = LoggerFactory.getLogger(Action.class);
    private final String name;

    public final Future<HttpResponse> apply(HttpRequest request) {
        final HttpResponse httpResponse = new DefaultHttpResponse(HTTP_1_1, OK);

        try {
            return packResponse(action(request, httpResponse), httpResponse);
        } catch (Exception e) {
            logger.error("process action err:", e);
            return packResponse("err", httpResponse);
        }
    }

    protected abstract String action(HttpRequest req, HttpResponse resp) throws Exception;

    public Action(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    protected Future<HttpResponse> packResponse(String content, HttpResponse httpResponse) {
        httpResponse.setHeader(ACCEPT_CHARSET, "UTF-8");
        httpResponse.setContent(ChannelBuffers.wrappedBuffer(content.getBytes()));
        Future<HttpResponse> future = Future.value(httpResponse);
        return future;
    }

    /**
     * pack request, get the parameters
     *
     * @param request the http request
     * @return a json object contains all the parameters
     * @throws Exception exception
     * @since modified by george cao at 2011-01-20
     */
    protected HashMap packRequest(HttpRequest request) {

        if (!request.getMethod().equals(GET)) {
            QueryStringDecoder decoder = new QueryStringDecoder("/?" + request.getContent().toString(Charset.forName("UTF-8")) + "&");

            return traversalDecoder(decoder);
        } else {
            return parseQueryString(request);
        }
    }

    /**
     * parse the query string parameters.
     *
     * @param request http request
     * @return a json object contains the parameters
     * @author georgecao
     */
    protected HashMap parseQueryString(HttpRequest request) {
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri(), Charset.forName("UTF-8"));
        return traversalDecoder(decoder);
    }

    /**
     * traversal the decoder
     *
     * @param decoder the decoder
     * @author georgecao
     */
    private HashMap traversalDecoder(QueryStringDecoder decoder) {
        HashMap json = new HashMap();

        Iterator<Map.Entry<String, List<String>>> iterator = decoder.getParameters().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> entry = iterator.next();
            json.put(entry.getKey(), entry.getValue().get(0));
        }
        return json;
    }

    protected String toString(Map map, String key) {
        try {
            return (String) map.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    protected int toInt(Map map, String key) {
        try {
            return Integer.parseInt((String) map.get(key));
        } catch (Exception e) {
            return -1;
        }
    }

    protected long toLong(HashMap map, String key) {
        try {
            return Long.parseLong((String) map.get(key));
        } catch (Exception e) {
            return -1;
        }
    }

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
