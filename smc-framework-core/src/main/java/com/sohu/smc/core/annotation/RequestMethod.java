package com.sohu.smc.core.annotation;

/**
 * User: zhangsuozhu
 * Date: 13-1-15
 * Time: 下午6:50
 */
public enum RequestMethod {
    GET(0), POST(1), HEAD(2), PUT(3), PATCH(4), DELETE(5), OPTIONS(6), TRACE(7);
    private int value;

    private RequestMethod(int i) {
        this.value = (int) Math.pow(2, i);
    }

    public int getValue() {
        return value;
    }
}
