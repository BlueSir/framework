package com.sohu.smc.sample.resource;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-1-30
 * Time: 上午9:53
 * To change this template use File | Settings | File Templates.
 */
public class CommandHelloWorld extends HystrixCommand<String> {
    private final String name;

    public CommandHelloWorld(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("hi"));
        this.name = name;
    }

    @Override
    protected String run() {
        return "Hello " + name + "!" + new Date();
    }
}
