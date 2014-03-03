package com.sohu.smc.core.config;

import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class RedisConfiguration {
    @JsonProperty
    private String hash = "";

    @JsonProperty
    private String conn = "";

    public String getHash() {
        return hash;
    }

    public String getConn() {
        return conn;
    }
}
