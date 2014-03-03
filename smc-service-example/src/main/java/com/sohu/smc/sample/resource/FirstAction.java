package com.sohu.smc.sample.resource;


import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.sohu.smc.core.annotation.RequestMapping;
import com.sohu.smc.core.annotation.RequestMatcher;
import com.sohu.smc.core.annotation.RequestMethod;
import com.sohu.smc.core.server.ActionSupport;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.util.HashMap;

/**
 * User: zhangsuozhu
 * Date: 13-1-14
 * Time: 下午6:11
 */
@RequestMapping(value = "/")
public class FirstAction extends ActionSupport {
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.OPTIONS}, matcher = RequestMatcher.SINGLE)
    public String list(HttpRequest r, HttpResponse resp) {
        HashMap<String, Object> map = packRequest(r);
        System.out.println(toString(map,("key")));        //+(34/0)
        return "list"+map;
    }

    @RequestMapping
    public String add(HttpRequest r, HttpResponse resp) {

        return "add";
    }
    @RequestMapping(value = "action.go")
    public String action(HttpRequest req, HttpResponse resp) throws Exception {

        return "hello";
    }

    @RequestMapping
    public String logtest(){

//        HystrixCommandMetrics hi = HystrixCommandMetrics.getInstance(HystrixCommandKey.Factory.asKey(CommandHelloWorld.class.getSimpleName()));

        String s = new CommandHelloWorld("hi").execute();

        return "OK";
    }

}
