package com.sohu.smc.core.filters;

import com.twitter.finagle.Service;
import com.twitter.finagle.SimpleFilter;
import com.twitter.util.Future;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sohu.smc.common.util.SystemKey.server_resources;
import static com.sohu.smc.common.util.SystemParam.get;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created with IntelliJ IDEA.
 * User: Qinqd
 * Date: 12-8-2
 * Time: 下午5:28
 */
public class FileService extends SimpleFilter<HttpRequest, HttpResponse> {
    //lifted from tiscaf - http://gaydenko.com/scala/tiscaf/httpd/
    private final static Map<String, String> exts = new HashMap<String, String>();
    public final static String basepath = get(server_resources) + "/web/";

    static {
        exts.put("html", "text/html");
        exts.put("htm", "text/html");
        exts.put("js", "application/x-javascript");
        exts.put("css", "text/css ");
        exts.put("shtml", "text/html");
        exts.put("gif", "image/gif");
        exts.put("ico", "image/x-icon");
        exts.put("jpeg", "image/jpeg ");
        exts.put("jpg", "image/jpeg ");
        exts.put("png", "image/png");
        exts.put("pdf", "application/pdf");
        exts.put("zip", "application/zip");
        exts.put("xhtml", "application/xhtml+xml");
        exts.put("xht", "application/xhtml+xml");
        exts.put("svg", "image/svg+xml");
        exts.put("svgz", "image/svg+xml");
        exts.put("tiff", "image/tiff");
        exts.put("tif", "image/tiff");
        exts.put("djvu", "image/vnd.djvu");
        exts.put("djv", "image/vnd.djvu");
        exts.put("bmp", "image/x-ms-bmp");
        exts.put("asc", "text/plain");
        exts.put("txt", "text/plain");
        exts.put("text", "text/plain");
        exts.put("diff", "text/plain");
        exts.put("scala", "text/plain");
        exts.put("xml", "application/xml");
        exts.put("xsl", "application/xml");
        exts.put("tgz", "application/x-gtar");
        exts.put("jar", "application/java-archive");
        exts.put("class", "application/java-vm");
        exts.put("flac", "application/x-flac");
        exts.put("ogg", "application/ogg");
        exts.put("wav", "audio/x-wav");
        exts.put("pgp", "application/pgp-signatur");
        exts.put("ps", "application/postscript");
        exts.put("eps", "application/postscript");
        exts.put("rar", "application/rar");
        exts.put("rdf", "application/rdf+xml");
        exts.put("rss", "application/rss+xml");
        exts.put("torrent", "application/x-bittorrent");
        exts.put("deb", "application/x-debian-package");
        exts.put("udeb", "application/x-debian-package");
        exts.put("dvi", "application/x-dvi");
        exts.put("gnumeric", "application/x-gnumeric");
        exts.put("iso", "application/x-iso9660-image");
        exts.put("jnlp", "application/x-java-jnlp-file");
        exts.put("latex", "application/x-latex");
        exts.put("rpm", "application/x-redhat-package-manager");
        exts.put("tar", "application/x-tar");
        exts.put("texinfo", "application/x-texinfo");
        exts.put("texi", "application/x-texinfo");
        exts.put("man", "application/x-troff-man");
        exts.put("h++", "text/x-c++hdr");
        exts.put("hpp", "text/x-c++hdr");
        exts.put("hxx", "text/x-c++hdr");
        exts.put("hh", "text/x-c++hdr");
        exts.put("c++", "text/x-c++src");
        exts.put("cpp", "text/x-c++src");
        exts.put("cxx", "text/x-c++src");
        exts.put("cc", "text/x-c++src");
        exts.put("h", "text/x-chdr");
        exts.put("hs", "text/x-haskell");
        exts.put("java", "text/x-java");
        exts.put("lhs", "text/x-literate-haskell");
        exts.put("pas", "text/x-pascal");
        exts.put("py", "text/x-python");
        exts.put("xul", "application/vnd.mozilla.xul+xml");
        exts.put("odc", "application/vnd.oasis.opendocument.chart");
        exts.put("odb", "application/vnd.oasis.opendocument.database");
        exts.put("odf", "application/vnd.oasis.opendocument.formula");
        exts.put("odg", "application/vnd.oasis.opendocument.graphics");
        exts.put("odi", "application/vnd.oasis.opendocument.image");
        exts.put("odp", "application/vnd.oasis.opendocument.presentation");
        exts.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
        exts.put("odt", "application/vnd.oasis.opendocument.text");
        exts.put("abw", "application/x-abiword");
    }

    static List gzipable = new ArrayList();

    static {
        gzipable.add("text/html");
        gzipable.add("application/x-javascript");
        gzipable.add("text/css ");
        gzipable.add("text/plain");
        gzipable.add("application/xml");
        gzipable.add("application/xhtml+xml");
        gzipable.add("image/svg+xml");
        gzipable.add("application/rdf+xml");
        gzipable.add("application/rss+xml");
        gzipable.add("text/x-c++hdr");
        gzipable.add("text/x-c++src");
        gzipable.add("text/x-chdr");
        gzipable.add("text/x-haskell");
        gzipable.add("text/x-java");
        gzipable.add("text/x-python"
        );
    }

    //more holes than swiss cheese
    public boolean validPath(String path) {
//        System.out.println("...static file, file[" + basepath + path + "]");
        if (path.indexOf('.') == -1) return false;
        File file = new File(basepath, path);
        if (file.toString().contains("..")) return false;
        if (!file.exists() || file.isDirectory()) return false;
        if (!file.canRead()) return false;
//        System.out.println("===>>>> exit file...");
        return true;
    }

    public Future apply(HttpRequest request, Service<HttpRequest, HttpResponse> service) {
        if (validPath(request.getUri())) {

            File file = new File(basepath, request.getUri());
            FileInputStream fh = null;
            try {
                fh = new FileInputStream(file);
                byte[] b = new byte[(int)file.length()]; //直接转换  modify by zhu
                fh.read(b);

                HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
                String mtype = exts.get(file.getName().substring(file.getName().lastIndexOf(".") + 1));
                if (mtype == null) {
                    mtype = "application/octet-stream";
                }
                response.setStatus(OK);
                response.setHeader("Content-Type", mtype);
                response.setContent(ChannelBuffers.copiedBuffer(b));
                return Future.value(response);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fh != null) {
                    try {
                        fh.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        fh = null;
                    }
                }
            }

            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
            response.setContent(ChannelBuffers.copiedBuffer("ERROR".getBytes()));
            return Future.value(response);
        } else {
            return service.apply(request);
        }
    }
}


