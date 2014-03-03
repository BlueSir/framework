package com.sohu.smc.core.config;

import com.sohu.smc.common.util.IpUtil;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

// TODO: 11/7/11 <coda> -- document HttpConfiguration

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "CanBeFinal"})
public class HttpConfiguration {

    @Min(1025)
    @Max(65535)
    @JsonProperty
    private int port = 8080;

    @Min(1025)
    @Max(65535)
    @JsonProperty
    private int adminPort = 8081;

    @Min(1025)
    @Max(65535)
    @JsonProperty
    private int monitorPort = 8082;

    @Valid
    @NotNull
    @JsonProperty
    private String serviceName;

    @Valid
    @NotNull
    @JsonProperty
    private UrlConfiguration scribeLog = new UrlConfiguration();

    @Valid
    @JsonProperty
    private String desc = "http server";

    @Valid
    @NotNull
    @JsonProperty
    private String staticRoot;

    //实例运行时所在机器ip
    private String ip;

    //实例运行时所在机器hostname
    private String hostname;

    public String getDesc() {
        return desc;
    }

    public int getPort() {
        return port;
    }

    public int getAdminPort() {
        return adminPort;
    }

    public UrlConfiguration getScribeLog() {
        return scribeLog;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        if (ip == null) {
            this.ip = IpUtil.getIp();
        } else {
            this.ip = ip;
        }
    }

    public void setStaticRoot(String staticRoot) {
        this.staticRoot = staticRoot;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getStaticRoot() {
        return staticRoot;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAdminPort(int adminPort) {
        this.adminPort = adminPort;
    }

    public void setMonitorPort(int monitorPort) {
        this.monitorPort = monitorPort;
    }

    public int getMonitorPort() {
        return monitorPort;
    }
}
