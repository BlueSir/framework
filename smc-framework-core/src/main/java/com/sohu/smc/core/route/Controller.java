package com.sohu.smc.core.route;

import com.twitter.util.Future;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.ACCEPT_CHARSET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * handler的容器
 */
public class Controller {

    private final String serviceName;
    private RouteResolver routeResolver;

    public Controller(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setRouteResolver(RouteResolver routeResolver) {
        this.routeResolver = routeResolver;
    }

    public RouteResolver getRouteResolver() {

        return routeResolver;
    }

    public Future<HttpResponse> process(HttpRequest httpRequest) {
        Request request = new Request(httpRequest, routeResolver);
        Response response = new Response();

        Action action = routeResolver.resolve(request);
        Object result = action.invoke(request, response);

        final HttpResponse httpResponse = new DefaultHttpResponse(HTTP_1_1, OK);

        if (result != null){
            httpResponse.setHeader(ACCEPT_CHARSET, "UTF-8");
            httpResponse.setHeader(HttpHeaders.Names.CONTENT_TYPE, ContentType.TEXT_PLAIN);
            if(response.getContentType() != null){
                httpResponse.setHeader(HttpHeaders.Names.CONTENT_TYPE, response.getContentType());
            }
            if(response.getResponseStatus() != null){
                httpResponse.setStatus(response.getResponseStatus());
            }
            for (String name : response.getHeaderNames())
            {
                for (String value : response.getHeaders(name))
                {
                    httpResponse.addHeader(name, value);
                }
            }
            httpResponse.setContent(ChannelBuffers.wrappedBuffer(((String)result).getBytes()));
            Future<HttpResponse> future = Future.value(httpResponse);
            return future;
        }else{
            Future<HttpResponse> future = Future.value(httpResponse);
            return future;
        }
//
    }

}
