package com.sohu.smc.core.server;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.sohu.smc.core.server.builer.SingleActionMapping;
import com.twitter.util.Future;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import static com.sohu.smc.core.server.util.ServerUtil.isMethodAllowed;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.ACCEPT_CHARSET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * handler的容器
 */
public class Controller {

    private final String serviceName;
    private final SingleActionMapping singleActionMapping;


    public Controller(String serviceName) {
        this.serviceName = serviceName;
        singleActionMapping = SingleActionMapping.getInstance();
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        //  startMetricsMonitor();
    }


    private String getActionName(String uri) {
        int pos = uri.indexOf("?");
        if (pos > -1) {
            return uri.substring(0, pos);
        }
        return uri;
    }

    public Future<HttpResponse> process(HttpRequest request) {

        /**
         *   action 的处理
         *   1、查找single的
         *   2、匹配parameter的，这个需要遍历
         *   3、匹配regex的，这个需要遍历

         */
        final String uri = getActionName(request.getUri());
        Action action = singleActionMapping.getAction(uri);


        final HttpResponse httpResponse = new DefaultHttpResponse(HTTP_1_1, OK);

        if (action != null) {
            String result;
            if (isMethodAllowed(request.getMethod(), action.getMethod())) {
                try {

                    Action previouslySavedOrder = new ActionCommand(uri).execute();

                    result = previouslySavedOrder.execute(request, httpResponse);


                    //     action.incrementCount();
                    // setResponseCode(httpResponse, OK);
                } catch (Exception e) {
                    httpResponse.setStatus(INTERNAL_SERVER_ERROR);
                    result = ExceptionUtils.getFullStackTrace(e);

                }
            } else {
                result = "METHOD_NOT_ALLOWED";
                httpResponse.setStatus(METHOD_NOT_ALLOWED);
            }


//            httpResponse.setHeader(HttpHeaders.Names.CONTENT_TYPE, ContentType.TEXT_PLAIN);

            if (httpResponse.getHeader(HttpHeaders.Names.CONTENT_TYPE) == null) {
                // httpResponse.setHeader(HttpHeaders.Names.CONTENT_TYPE, response.getContentType());
                httpResponse.setHeader(HttpHeaders.Names.CONTENT_TYPE, ContentType.TEXT_PLAIN);
            }
            if (httpResponse.getHeader(HttpHeaders.Names.ACCEPT_CHARSET) == null) {
                // httpResponse.setHeader(HttpHeaders.Names.CONTENT_TYPE, response.getContentType());
                httpResponse.setHeader(ACCEPT_CHARSET, "UTF-8");
            }
//            if(response.getResponseStatus() != null){
//                httpResponse.setStatus(OK);
//            }
//            for (String name : httpResponse.getHeaderNames())
//            {
//                for (String value : response.getHeaders(name))
//                {
//                    httpResponse.addHeader(name, value);
//                }
//            }
            httpResponse.setContent(ChannelBuffers.wrappedBuffer((result).getBytes()));
            Future<HttpResponse> future = Future.value(httpResponse);
            return future;
        } else {
            Future<HttpResponse> future = Future.value(httpResponse);
            return future;
        }
//
    }


}
