package com.sohu.smc.core.config;

import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class UrlConfiguration {
    @JsonProperty
    private String conn = "";

    public String getConn() {
        return conn;
    }
}
