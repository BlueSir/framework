package com.sohu.smc.litecore;

import com.twitter.ostrich.stats.Stats;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class Action {

    protected abstract String action(HttpRequest req, HttpResponse resp) throws Exception;

    /**
     * parse the query string parameters.
     *
     * @param request http request
     * @return a json object contains the parameters
     * @author georgecao
     */
    public HashMap parseQueryString(HttpRequest request) {
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


    /**
     * pack request, get the parameters
     *
     * @param request the http request
     * @return a json object contains all the parameters
     * @throws Exception exception
     * @since modified by george cao at 2011-01-20
     */
    public HashMap packRequest(HttpRequest request) {

        if (!request.getMethod().equals(HttpMethod.GET)) {
            QueryStringDecoder decoder = new QueryStringDecoder("/?" + request.getContent().toString(Charset.forName("UTF-8")) + "&");

            return traversalDecoder(decoder);
        } else {
            return parseQueryString(request);
        }
    }

    public String toString(HashMap map, String key) {
        try {
            return (String) map.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public int toInt(HashMap map, String key) {
        try {
            return Integer.parseInt((String) map.get(key));
        } catch (Exception e) {
            return -1;
        }
    }

    public long toLong(HashMap map, String key) {
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
