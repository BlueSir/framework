package com.sohu.smc.common.util;

import java.util.Properties;

import static com.sohu.smc.common.util.SystemKey.*;

/**
 * 系统参数工具类
 * User: shijinkui
 * Date: 12-9-3
 * Time: 下午2:06
 * To change this template use File | Settings | File Templates.
 */
public class SystemParam {
    private final static Properties properties;

    static {
        properties = System.getProperties();
    }

    public static String get(SystemKey key) {
        return System.getProperty(key.key);
    }

    /**
     * 程序jar以及相关依赖的目录
     * 如：/opt/smc/apps/server/server_apps/broker_1
     *
     * @return
     */
    public static String getLibHome() {
        return properties.getProperty(host_home.key);
    }

    /**
     * 运行时的log目录
     * /opt/smc/log/server
     *
     * @return
     */
    public static String getRuntimeLogHome() {
        return properties.getProperty(server_log_home.key);
    }

    /**
     * 静态文件的根目录的上一级目录
     *
     * @return
     */
    public static String getWebHome() {
        return properties.getProperty(server_resources.key) + "/web/";
    }

    public static String getIp() {
        return properties.getProperty(server_ip.key);
    }

    /**
     * 资源文件目录，html和系统配置文件
     *
     * @return
     */
    public static String getResourceHome() {
        return properties.getProperty(server_resources.key);
    }

    /**
     * 本java实例名
     *
     * @return
     */
    public static String getInstanceName() {
        return properties.getProperty(server_name.key, "serviceIntance-" + System.currentTimeMillis());
    }

    /**
     * main函数参数列表
     *
     * @return
     */
    public static String getMainArgs() {
        return properties.getProperty(server_args.key);
    }

    /**
     * java虚拟机参数列表
     *
     * @return
     */
    public static String getJVMArgs() {
        return properties.getProperty(server_jvm_args.key);
    }
}


