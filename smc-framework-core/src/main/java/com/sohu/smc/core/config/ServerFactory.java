package com.sohu.smc.core.config;

import com.sohu.smc.common.util.IpUtil;
import com.sohu.smc.common.util.SystemParam;
import com.sohu.smc.core.filters.FileService;
import com.twitter.finagle.Filter;
import com.twitter.finagle.Service;
import com.twitter.finagle.SimpleFilter;
import com.twitter.finagle.builder.Server;
import com.twitter.finagle.builder.ServerBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.finagle.stats.OstrichStatsReceiver;
import com.twitter.finagle.tracing.ConsoleTracer;
import com.twitter.ostrich.admin.AdminHttpService;
import com.twitter.ostrich.admin.RuntimeEnvironment;
import com.twitter.ostrich.admin.TimeSeriesCollectorFactory;
import com.twitter.ostrich.stats.Stats;
import com.twitter.util.Duration;
import com.twitter.util.Future;
import com.yammer.metrics.HealthChecks;
import com.yammer.metrics.core.HealthCheck;
import com.yammer.metrics.util.DeadlockHealthCheck;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.sohu.smc.common.util.SystemKey.server_ip;

public class ServerFactory implements ServerLifecycle {
    private final Logger log = LoggerFactory.getLogger(ServerFactory.class.getName());
    private final Configuration config;
    private final Environment env;
    private Server server;
    private AdminHttpService admin;

    public ServerFactory(Configuration config, Environment env) {
        this.env = env;
        this.config = config;
        this.config.getHttpConfiguration().setHostname(IpUtil.getHostname());
        this.config.getHttpConfiguration().setIp(SystemParam.get(server_ip));
    }

    private boolean initialization(final Environment env) {

        HealthChecks.register(new DeadlockHealthCheck());
        for (HealthCheck healthCheck : env.getHealthChecks()) {
            HealthChecks.register(healthCheck);
        }

        if (env.getHealthChecks().isEmpty()) {
            log.warn('\n' +
                    "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n" +
                    "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n" +
                    "!    THIS SERVICE HAS NO HEALTHCHECKS. THIS MEANS YOU WILL NEVER KNOW IF IT    !\n" +
                    "!    DIES IN PRODUCTION, WHICH MEANS YOU WILL NEVER KNOW IF YOU'RE LETTING     !\n" +
                    "!     YOUR USERS DOWN. YOU SHOULD ADD A HEALTHCHECK FOR EACH DEPENDENCY OF     !\n" +
                    "!     YOUR SERVICE WHICH FULLY (BUT LIGHTLY) TESTS YOUR SERVICE'S ABILITY TO   !\n" +
                    "!      USE THAT SERVICE. THINK OF IT AS A CONTINUOUS INTEGRATION TEST.         !\n" +
                    "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n" +
                    "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            );
        }


        return true;
    }

    @Override
    public void start(final Environment env) {

        initialization(env);
        // env.initController();
        //处理请求的链路
        Service<HttpRequest, HttpResponse> httpserver = new Service<HttpRequest, HttpResponse>() {
            public Future<HttpResponse> apply(HttpRequest request) {
                return env.getController().process(request);
            }
        };

        InetSocketAddress serviceAddress = new InetSocketAddress(config.getHttpConfiguration().getIp(), config.getHttpConfiguration().getPort());

        //filter chain
        CopyOnWriteArrayList<SimpleFilter<HttpRequest, HttpResponse>> chain = env.getFilters();
        chain.add(new FileService());
        Filter chains = null;
        for (SimpleFilter<HttpRequest, HttpResponse> filter : chain) {
            if (chains != null) {
                chains = chains.andThen(filter);
            } else {
                chains = filter;
            }
        }
        Server server = ServerBuilder.safeBuild(
                chains.andThen(httpserver),
                ServerBuilder.get()
                        .codec(Http.get())
                        .bindTo(serviceAddress)
                        .reportTo(new OstrichStatsReceiver(Stats.get(""))) // export host-level load data to ostrich
                        .tracerFactory(ConsoleTracer.factory())
                                //.tracerFactory(ZipkinTracer.apply(conf.getTraceHost(), conf.getTracePort(), new NullStatsReceiver(),
                                //scala.Float.unbox(conf.getTceRate())))
                                //                        .logger(Logger.getLogger("http"))
                        .name(config.getHttpConfiguration().getServiceName()));

        try {
            AdminHttpService admin = new AdminHttpService(
                    config.getHttpConfiguration().getAdminPort(),
                    123,
                    new RuntimeEnvironment(this));
            TimeSeriesCollectorFactory seriesCollectorFactory = new TimeSeriesCollectorFactory();
            seriesCollectorFactory.apply(Stats.get(""), admin).start();
            admin.start();
            this.admin = admin;
        } catch (Exception e) {
            server.close(Duration.zero());
            log.error("admin boot error, maybe the port[" + (config.getHttpConfiguration().getPort() + 1) + "] is used.");
            System.exit(0);
        }

//        InetSocketAddress serviceAddressStream = new InetSocketAddress(config.getHttpConfiguration().getIp(), config.getHttpConfiguration().getMonitorPort());
//        StreamServer streamServer = new StreamServer(serviceAddressStream);
//        streamServer.startServer();

        this.server = server;
        //regist twitter service
//        ServiceRegister.regist(config.getHttpConfiguration().getServiceName(), serviceAddress);
    }


    @Override
    public void stop() {
        log.info("*********** stop @ServerFactory@ ************");
        this.server.close(Duration.zero());
        this.admin.shutdown();
    }
}
