package com.sohu.smc.core.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static com.sohu.smc.common.util.SystemKey.server_resources;
import static com.sohu.smc.common.util.SystemParam.get;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * fetches the header information (tags set, common tags) for a given time frame
 *
 * @author cgheorghe
 */
@Deprecated
public class StaticFileAction extends Action {
    private static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";
    public static final String staticroot = get(server_resources);

    public StaticFileAction() {
        super("staticfile");
    }

    public String action(final HttpRequest request, HttpResponse response) throws Exception {
        return "";
    }

    public String action(final HttpRequest request, HttpResponse response, final Channel chan) throws Exception {
        addCounter("getDetail");
        final String uri = request.getUri();
        if ("/favicon.ico".equals(uri)) {
            sendFile(staticroot + "/favicon.ico", 31536000 /*=1yr*/, chan, request, response);
            return "";
        }
        if (uri.length() < 3) {  // Must be at least 3 because of the "/s/".
            throw new Exception("URI too short <code>" + uri + "</code>");
        }
        // Cheap security check to avoid directory traversal attacks.
        // TODO(tsuna): This is certainly not sufficient.
        if (uri.indexOf("..", 3) > 0) {
            response.setStatus(HttpResponseStatus.valueOf(404));
            response.setContent(ChannelBuffers.copiedBuffer("Malformed URI <code>" + uri + "</code>", CharsetUtil.UTF_8));
            return "";
        }
        final int questionmark = uri.indexOf('?', 3);
        final int pathend = questionmark > 0 ? questionmark : uri.length();
        sendFile(staticroot + uri.substring(3, pathend),
                uri.contains("nocache") ? 0 : 31536000 /*=1yr*/, chan, request, response);
        return "";
    }

    /**
     * Send a file (with zero-copy) to the client with a 200 OK status.
     * This method doesn't provide any security guarantee.  The caller is
     * responsible for the argument they pass in.
     *
     * @param path    The path to the file to send to the client.
     * @param max_age The expiration time of this entity, in seconds.  This is
     *                not a timestamp, it's how old the resource is allowed to be in the client
     *                cache.  See RFC 2616 section 14.9 for more information.  Use 0 to disable
     *                caching.
     */
    public void sendFile(final String path,
                         final int max_age, final Channel chan, final HttpRequest request, final HttpResponse response) throws IOException {
        sendFile(OK, path, max_age, chan, request, response);
    }

    /**
     * Send a file (with zero-copy) to the client.
     * This method doesn't provide any security guarantee.  The caller is
     * responsible for the argument they pass in.
     *
     * @param status  The status of the request (e.g. 200 OK or 404 Not Found).
     * @param path    The path to the file to send to the client.
     * @param max_age The expiration time of this entity, in seconds.  This is
     *                not a timestamp, it's how old the resource is allowed to be in the client
     *                cache.  See RFC 2616 section 14.9 for more information.  Use 0 to disable
     *                caching.
     */
    public void sendFile(final HttpResponseStatus status,
                         final String path,
                         final int max_age, final Channel channel, final HttpRequest request, final HttpResponse response) throws IOException {
        if (max_age < 0) {
            throw new IllegalArgumentException("Negative max_age=" + max_age
                    + " for path=" + path);
        }
        if (channel == null) {
            return;
        }
        if (!channel.isConnected()) {
            //done();
            return;
        }
        RandomAccessFile file;
        try {
            file = new RandomAccessFile(path, "r");
        } catch (FileNotFoundException e) {
            //logWarn("File not found: " + e.getMessage());

            response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
            // TODO(tsuna): Server, X-Backend, etc. headers.
            ChannelBuffer buf = ChannelBuffers.copiedBuffer("not found", CharsetUtil.UTF_8);
            response.setContent(buf);
            final boolean keepalive = HttpHeaders.isKeepAlive(request);
            if (keepalive) {
                HttpHeaders.setContentLength(response, buf.readableBytes());
            }
            final ChannelFuture future = channel.write(response);
            if (!keepalive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
            e.printStackTrace();
            return;
        }
        final long length = file.length();
        {
            final String mimetype = guessMimeTypeFromUri(path);
            response.setHeader(HttpHeaders.Names.CONTENT_TYPE,
                    mimetype == null ? "text/plain" : mimetype);
            final long mtime = new File(path).lastModified();
            if (mtime > 0) {
                response.setHeader(HttpHeaders.Names.AGE,
                        (System.currentTimeMillis() - mtime) / 1000);
            } else {
                //logWarn("Found a file with mtime=" + mtime + ": " + path);
            }
            response.setHeader(HttpHeaders.Names.CACHE_CONTROL,
                    max_age == 0 ? "no-cache" : "max-age=" + max_age);
            HttpHeaders.setContentLength(response, length);
            channel.write(response);
        }
        final FileRegion region = new DefaultFileRegion(file.getChannel(),
                0, length);
        final ChannelFuture future = channel.write(region);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) {
                region.releaseExternalResources();
                //done();
            }
        });
        if (!HttpHeaders.isKeepAlive(request)) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * Attempts to guess the MIME type by looking at the URI requested.
     *
     * @param uri The URI from which to infer the MIME type.
     */
    private static String guessMimeTypeFromUri(final String uri) {
        final int questionmark = uri.indexOf('?', 1);  // 1 => skip the initial /
        final int end = (questionmark > 0 ? questionmark : uri.length()) - 1;
        if (end < 5) {  // Need at least: "/a.js"
            return null;
        }
        final char a = uri.charAt(end - 3);
        final char b = uri.charAt(end - 2);
        final char c = uri.charAt(end - 1);
        switch (uri.charAt(end)) {
            case 'g':
                return a == '.' && b == 'p' && c == 'n' ? "image/png" : null;
            case 'l':
                return a == 'h' && b == 't' && c == 'm' ? HTML_CONTENT_TYPE : null;
            case 's':
                if (a == '.' && b == 'c' && c == 's') {
                    return "text/css";
                } else if (b == '.' && c == 'j') {
                    return "text/javascript";
                } else {
                    break;
                }
            case 'f':
                return a == '.' && b == 'g' && c == 'i' ? "image/gif" : null;
            case 'o':
                return a == '.' && b == 'i' && c == 'c' ? "image/x-icon" : null;
        }
        return null;
    }

}


