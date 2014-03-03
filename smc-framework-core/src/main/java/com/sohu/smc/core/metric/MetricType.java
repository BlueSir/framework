package com.sohu.smc.core.metric;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-1-30
 * Time: 下午3:36
 * To change this template use File | Settings | File Templates.
 */
public enum MetricType {
    hystrix("hystrix"), ostrich("ostrich");

    public String type;
    MetricType(String h) {
        this.type = h;
    }
}
