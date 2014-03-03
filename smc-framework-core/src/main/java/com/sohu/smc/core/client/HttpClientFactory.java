package com.sohu.smc.core.client;

import org.apache.http.client.HttpClient;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.params.BasicHttpParams;

import java.util.concurrent.TimeUnit;

import static org.apache.http.client.params.ClientPNames.COOKIE_POLICY;
import static org.apache.http.params.CoreConnectionPNames.*;

public class HttpClientFactory {
    private final HttpClientConfiguration configuration;

    public HttpClientFactory(HttpClientConfiguration configuration) {
        this.configuration = configuration;
    }

    public HttpClient build() {
        final BasicHttpParams params = new BasicHttpParams();

        // TODO: 11/16/11 <coda> -- figure out the full set of options to support

        if (!configuration.isCookiesEnabled()) {
            params.setParameter(COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);
        } else {
            params.setParameter(COOKIE_POLICY, CookiePolicy.BEST_MATCH);
        }

        final Integer timeout = (int) configuration.getTimeout().toMilliseconds();
        params.setParameter(SO_TIMEOUT, timeout);
        params.setParameter(CONNECTION_TIMEOUT, timeout);

        params.setParameter(TCP_NODELAY, Boolean.TRUE);
        params.setParameter(STALE_CONNECTION_CHECK, Boolean.FALSE);

        final InstrumentedClientConnManager manager = new InstrumentedClientConnManager(
                SchemeRegistryFactory.createDefault(),
                configuration.getTimeToLive().toMilliseconds(),
                TimeUnit.MILLISECONDS
        );
        manager.setDefaultMaxPerRoute(configuration.getMaxConnections());
        manager.setMaxTotal(configuration.getMaxConnections());

        return new InstrumentedHttpClient(manager, params);
    }
}