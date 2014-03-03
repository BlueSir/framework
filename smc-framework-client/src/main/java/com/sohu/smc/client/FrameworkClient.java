package com.sohu.smc.client;

import com.twitter.finagle.Service;
import com.twitter.util.Duration;
import com.twitter.util.FutureEventListener;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static com.twitter.util.Duration.apply;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * framework client.
 * <p/>
 * User: shijinkui
 * Date: 12-9-28
 * Time: 上午10:45
 * To change this template use File | Settings | File Templates.
 */
public class FrameworkClient {

    private final Duration default_timeout = apply(2, TimeUnit.SECONDS);
    private final ConcurrentMap<String, Service<HttpRequest, HttpResponse>> httpholder = new ConcurrentHashMap<String, Service<HttpRequest, HttpResponse>>();
    private final String serviceName;

    public FrameworkClient(String serviceName) {
        this.serviceName = serviceName;

        Service<HttpRequest, HttpResponse> client = null;//todo 换成netflix的
//                ClientBuilder.safeBuild(ClientBuilder.get()
//                .cluster(ServiceRegister.getCluster(serviceName))
//                .codec(Http.get())
//                .connectionTimeout(default_timeout)
//                .retries(3)
//                .hostConnectionCoresize(2)
//                .requestTimeout(default_timeout)
//                .hostConnectionLimit(2));
        httpholder.put(serviceName, client);
    }


    public void asynGet(String uri, FutureEventListener<HttpResponse> future) throws Exception {

        Service<HttpRequest, HttpResponse> service = httpholder.get(serviceName);
        HttpRequest request = new DefaultHttpRequest(HTTP_1_1, GET, uri);
        service.apply(request).addEventListener(future);
    }


    /**
     * @param uri 路径
     * @return
     * @throws Exception
     */
    public HttpResponse syncGet(String uri) throws Exception {
        return syncGet(uri, default_timeout);
    }

    public HttpResponse syncGet(String uri, Duration duration) throws Exception {
        Service<HttpRequest, HttpResponse> service = httpholder.get(serviceName);

        HttpRequest request = new DefaultHttpRequest(HTTP_1_1, GET, uri);
        HttpResponse response = httpholder.get(serviceName).apply(request).apply(duration);
        return response;
    }
}
