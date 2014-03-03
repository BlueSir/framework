package com.sohu.smc.litecore;

/**
 * Created with IntelliJ IDEA.
 * User: Qinqd
 * Date: 12-11-16
 * Time: 下午4:31
 * To change this template use File | Settings | File Templates.
 */
public interface HandlerFunction {
    public byte[] get(String key);

    public boolean set(String key, int flag, int expire, byte[] data);

    public boolean add(String key, int flag, int expire, byte[] data);

    public String[] stats();


}
