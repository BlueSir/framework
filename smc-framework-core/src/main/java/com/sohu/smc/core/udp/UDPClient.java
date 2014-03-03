package com.sohu.smc.core.udp;

import com.sohu.smc.common.util.PropertyUtil;
import com.sohu.smc.core.metric.MetricType;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-1-29
 * Time: 下午4:03
 * To change this template use File | Settings | File Templates.
 */
public class UDPClient {

    private DatagramSocket dataSocket;
    private final SocketAddress address;


    public UDPClient() {
        Properties prop = PropertyUtil.load("service.properties", UDPClient.class);
        String server_port = prop.getProperty("udp.server.port");
        String server_address = prop.getProperty("udp.server.address");
        prop.clear();
        prop = null;

        address = new InetSocketAddress(server_address, Integer.parseInt(server_port));
        System.out.println(address);
        try {
            dataSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] data) {
        if (data == null || data.length < 1) {
            return;
        }

        if (!dataSocket.isConnected()) {
            try {
                dataSocket.close();
                dataSocket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        DatagramPacket dataPacket = null;
        try {
            dataPacket = new DatagramPacket(data, data.length, address); //todo final object
            dataSocket.send(dataPacket);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
