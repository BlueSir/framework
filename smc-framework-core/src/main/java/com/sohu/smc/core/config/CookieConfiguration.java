package com.sohu.smc.core.config;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class CookieConfiguration {
    @JsonProperty
    private String youkuSecret = "";

    @JsonProperty
    private String youkuSecret2 = "";

    @JsonProperty
    private String oldYoukuSecret = "";

    @Min(1)
    @Max(128)
    @JsonProperty
    private int version = 1;

    public String getYoukuSecret() {
        return youkuSecret;
    }

    public String getYoukuSecret2() {
        return youkuSecret2;
    }

    public String getOldYoukuSecret() {
        return oldYoukuSecret;
    }

    public int getVersion() {
        return version;
    }


}
