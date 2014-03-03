package com.sohu.smc.core.http;

import com.sohu.smc.common.util.MD5;
import com.sohu.smc.core.config.ServerFactory;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 12-9-4
 * Time: 下午6:23
 */
public class SystemAction extends Action {

    private final ServerFactory server;

    public SystemAction(ServerFactory server) {
        super("admin");
        this.server = server;
    }


    @Override
    protected String action(HttpRequest req, HttpResponse resp) throws Exception {
        Map map = packRequest(req);
        String source = toString(map, "md5");
        String mymd5 = MD5.crypt("password");

        boolean passed = source.equals(mymd5);

        if (passed) {

        }


        return null;
    }

}
