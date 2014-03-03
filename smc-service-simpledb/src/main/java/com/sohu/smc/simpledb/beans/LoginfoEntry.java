package com.sohu.smc.simpledb.beans;

/**
 * Created with IntelliJ IDEA.
 * User: huixiao200068
 * Date: 13-1-6
 * Time: 下午5:07
 * To change this template use File | Settings | File Templates.
 */
public class LoginfoEntry {
    private String key;
    private Integer value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String toString() {
        return key + " --> " + value;
    }
}
