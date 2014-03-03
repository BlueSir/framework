package com.sohu.smc.litecore;

import com.sohu.smc.litecore.http.WebService;
import com.twitter.util.Duration;
import scala.Option;

import java.util.concurrent.TimeUnit;

public class WebServerFactory {
    private static Duration millisecondsToDuration(long milliseconds) {
        return Duration.fromTimeUnit(milliseconds, TimeUnit.MILLISECONDS);
    }

    public static void createKestrelServer(String[] args,String listenAddress, int httpListenPort, int adminListenPort,
                 int clientTimeout,Controller httpController){
        Duration clientTimeOutDuration = (Duration) null;
        if(clientTimeout != -1){
             clientTimeOutDuration = millisecondsToDuration(clientTimeout);
        }
        WebService.makeServer(args, listenAddress, httpListenPort, adminListenPort, Option.apply(clientTimeOutDuration),
                httpController);
    }
}
