package com.sohu.smc.common.util;

/**
 * jvm param key.
 * configured in project: smc-product-scm/deploy/conf/xxxx.yaml
 * User: shijinkui
 * Date: 12-9-3
 * Time: 下午2:15
 * To change this template use File | Settings | File Templates.
 */
public enum SystemKey {
    host_home("host_home"),
    server_log_home("server_log_home"),
    server_resources("server_resources"),
    server_name("server_name"),
    server_ip("server_ip"),
    server_main_class("server_main_class"),
    server_args("server_args"),
    server_jvm_args("server_jvm_args");

    String key;

    SystemKey(String server_name) {
        this.key = server_name;
    }
}