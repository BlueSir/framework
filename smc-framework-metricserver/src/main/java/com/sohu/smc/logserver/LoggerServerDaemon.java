package com.sohu.smc.logserver;

import com.sohu.smc.common.util.SystemKey;
import com.sohu.smc.common.util.SystemParam;
import com.sohu.smc.logserver.process.SocksServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioEventLoopGroup;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-1-29
 * Time: 下午9:40
 * To change this template use File | Settings | File Templates.
 */
public class LoggerServerDaemon {
    private final int port;

    public LoggerServerDaemon(int port) {
        this.port = port;
    }

    public void start() {
        String ip = SystemParam.get(SystemKey.server_ip);
        Bootstrap b = new Bootstrap();
        b.group(new NioEventLoopGroup())
                .channel(NioDatagramChannel.class)
                .localAddress(ip, port)
                .handler(new SocksServerHandler());
        try {
            b.bind().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("server started.");
    }

    public static void main(String... args) {

        if (args == null || args.length < 1) {
            System.err.println("port is empty.");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        LoggerServerDaemon daemon = new LoggerServerDaemon(port);
        daemon.start();
    }

}
