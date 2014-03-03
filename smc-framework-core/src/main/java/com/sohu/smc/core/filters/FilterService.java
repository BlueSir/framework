package com.sohu.smc.core.filters;

import com.twitter.finagle.Service;
import com.twitter.finagle.SimpleFilter;
import com.twitter.util.Future;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 * encoding  filter
 * User: shijinkui
 * Date: 12-9-10
 * Time: 下午5:32
 * To change this template use File | Settings | File Templates.
 */
public class FilterService extends SimpleFilter<HttpRequest, HttpResponse> {

//    private final CopyOnWriteArrayList<Function> list = new CopyOnWriteArrayList<HttpRequest>();

    /**
     * This is the method to override/implement to create your own Filter.
     *
     * @param request the input request type
     * @param service a service that takes the output request type and the input response type
     */
    @Override
    public Future<HttpResponse> apply(HttpRequest request, Service<HttpRequest, HttpResponse> service) {

        System.out.println("filter 1111");

        return service.apply(request);
    }
}
