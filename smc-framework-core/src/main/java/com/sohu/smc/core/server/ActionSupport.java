package com.sohu.smc.core.server;

import com.sohu.smc.core.server.util.QueryStringDecoder;
import com.twitter.ostrich.stats.Stats;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.jboss.netty.handler.codec.http.HttpMethod.GET;

/**
 * User: zhangsuozhu
 * Date: 13-1-16
 * Time: 上午11:22
 */
public class ActionSupport {


    /**
     * pack request, get the parameters
     *
     * @param request the http request
     * @return a json object contains all the parameters
     * @throws Exception exception
     * @since modified by george cao at 2011-01-20
     */
    public static final HashMap packRequest(HttpRequest request) {

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
    public static final HashMap parseQueryString(HttpRequest request) {
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri(), Charset.forName("UTF-8"));
        return traversalDecoder(decoder);
    }

    /**
     * traversal the decoder
     *
     * @param decoder the decoder
     * @author georgecao
     */
    public static final HashMap traversalDecoder(QueryStringDecoder decoder) {
        HashMap json = new HashMap();

        Iterator<Map.Entry<String, List<String>>> iterator = decoder.getParameters().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> entry = iterator.next();
             List<String> value=entry.getValue();
            if(value==null){
                continue;
            }
            if(value.size()==1){
                json.put(entry.getKey(), value.get(0));     //如果只有一个值，直接把值放入，方便使用
            }  else {
                json.put(entry.getKey(), entry.getValue()); //如果有多个值，返回一个list
            }

        }
        return json;
    }

    /**
     * @param map
     * @param key
     * @return
     */
    public static final  String toString(Map map, String key) {
        try {
            return (map.get(key)).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static final  int toInt(Map map, String key) {
        try {
            return Integer.parseInt(toString(map,key));
        } catch (Exception e) {
            return -1;
        }
    }

    public static final  long toLong(HashMap map, String key) {
        try {
            return Long.parseLong(toString(map,key));
        } catch (Exception e) {
            return -1;
        }
    }


    public static final  void addCounter(String key, int count) {
        Stats.incr(key, count);
    }

    public static final  void addCounter(String key) {
        Stats.incr(key);
    }

}
