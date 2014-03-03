package com.sohu.smc.simpledb.action;

import com.sohu.smc.core.annotation.RequestMapping;
import com.sohu.smc.core.server.ActionSupport;
import com.sohu.smc.simpledb.resources.Monitor;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 * Created with IntelliJ IDEA.
 * User: huixiao200068
 * Date: 13-1-29
 * Time: 下午3:10
 * To change this template use File | Settings | File Templates.
 */

@RequestMapping(value = "/monitor")
public class MonitorAction extends ActionSupport {

    @RequestMapping
    public String getData(HttpRequest r, HttpResponse resp) throws Exception {
        return Monitor.getData();
    }

    @RequestMapping
    public String testData(HttpRequest req, HttpResponse resp) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for(int i=0; i<10; i++) {
            String data = Monitor.getData();
            System.out.println(data);
            sb.append(data).append(",");
            Thread.sleep(2000);
        }
        String content = sb.substring(0, sb.length()-1) + "]";
        return content;
    }
}
