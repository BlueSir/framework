package com.sohu.smc.core.http;

import com.twitter.util.Future;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.ACCEPT_CHARSET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 封裝httpresponse返回信息
 * <p/>
 * User: shijinkui
 * Date: 12-8-28
 * Time: 下午3:26
 * To change this template use File | Settings | File Templates.
 */
public final class ResponseErrorWrapper {

    private final static Logger log = LoggerFactory.getLogger(ResponseErrorWrapper.class.getName());
    private final static String format = "%s,%s,URI:%s";

    public static String _400(HttpRequest request, String info) {
        String ret = String.format(format, BAD_REQUEST, info, request.getUri());
        log.warn(ret);
        return ret;
    }

    public static String _500(HttpRequest request, String info) {
        String ret = String.format(format, INTERNAL_SERVER_ERROR, info, request.getUri());
        log.warn(ret);
        return ret;
    }

    public static String _404(HttpRequest request, String info) {
        String ret = String.format(format, NOT_FOUND, info, request.getUri());
        log.warn(ret);
        return ret;
    }

    public static String _200(HttpRequest request, String info) {
        String ret = String.format(format, OK, info, request.getUri());
        log.warn(ret);
        return ret;
    }

    public static String _503(HttpRequest request, String info) {
        String ret = String.format(format, SERVICE_UNAVAILABLE, info, request.getUri());
        log.warn(ret);
        return ret;
    }

    public static String _510(HttpRequest request, String info) {
        String ret = String.format(format, NOT_EXTENDED, info, request.getUri());
        log.warn(ret);
        return ret;
    }

    public static Future<HttpResponse> _400(HttpRequest request) {
        HttpResponse httpResponse = new DefaultHttpResponse(HTTP_1_1, NOT_FOUND);

        String ret = String.format(format, BAD_REQUEST, "path is less than 2.", request.getUri());
        httpResponse.setContent(ChannelBuffers.wrappedBuffer(ret.getBytes()));
        Future<HttpResponse> future = Future.value(httpResponse);

        log.warn(ret);

        return future;
    }

    public static Future<HttpResponse> _500(HttpRequest request) {
        HttpResponse httpResponse = new DefaultHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR);
        String ret = String.format(format, INTERNAL_SERVER_ERROR, "server is error", request.getUri());
        httpResponse.setContent(ChannelBuffers.wrappedBuffer(ret.getBytes()));
        Future<HttpResponse> future = Future.value(httpResponse);

        log.warn(ret);

        return future;
    }

    public static Future<HttpResponse> _404(HttpRequest request) {
        HttpResponse httpResponse = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND);

        String ret = String.format(format, NOT_FOUND, "not found.", request.getUri());
        httpResponse.setContent(ChannelBuffers.wrappedBuffer(ret.getBytes()));
        Future<HttpResponse> future = Future.value(httpResponse);

        log.warn(ret);

        return future;
    }


    public static Future<HttpResponse> _200(HttpRequest request) {
        HttpResponse httpResponse = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);


        String ret = String.format(format, OK, "it is ok.", request.getUri());
        httpResponse.setContent(ChannelBuffers.wrappedBuffer(ret.getBytes()));
        Future<HttpResponse> future = Future.value(httpResponse);

        log.info(ret);

        return future;
    }

    public static Future<HttpResponse> _503(HttpRequest request) {
        HttpResponse httpResponse = new DefaultHttpResponse(HTTP_1_1, SERVICE_UNAVAILABLE);

        String ret = String.format(format, SERVICE_UNAVAILABLE, "your request service name is wrong, the service is not exist.", request.getUri());
        httpResponse.setContent(ChannelBuffers.wrappedBuffer(ret.getBytes()));
        Future<HttpResponse> future = Future.value(httpResponse);

        log.warn(ret);

        return future;
    }

    public static Future<HttpResponse> _510(HttpRequest request) {
        HttpResponse httpResponse = new DefaultHttpResponse(HTTP_1_1, NOT_EXTENDED);

        String ret = String.format(format, NOT_EXTENDED, "not support this action.", request.getUri());
        httpResponse.setContent(ChannelBuffers.wrappedBuffer(ret.getBytes()));
        Future<HttpResponse> future = Future.value(httpResponse);

        log.warn(ret);

        return future;
    }

    public static Future<HttpResponse> packResponse(String content) {
        final HttpResponse httpResponse = new DefaultHttpResponse(HTTP_1_1, OK);
        httpResponse.setHeader(ACCEPT_CHARSET, "UTF-8");
        httpResponse.setContent(ChannelBuffers.wrappedBuffer(content.getBytes()));
        Future<HttpResponse> future = Future.value(httpResponse);
        return future;
    }


}
