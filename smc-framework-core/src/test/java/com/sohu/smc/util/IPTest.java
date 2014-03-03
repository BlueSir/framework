package com.sohu.smc.util;

import junit.framework.TestCase;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 12-9-3
 * Time: 下午1:51
 * To change this template use File | Settings | File Templates.
 */
public class IPTest extends TestCase {

    public void testIp() {
        String metric = "asfdasdfasdffasdf";
        metric = metric.substring(0, metric.indexOf("?"));
        System.out.println(metric);
    }
}
