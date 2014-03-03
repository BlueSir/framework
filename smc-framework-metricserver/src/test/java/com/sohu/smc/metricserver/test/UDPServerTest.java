package com.sohu.smc.metricserver.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-1-29
 * Time: 下午4:04
 * To change this template use File | Settings | File Templates.
 */
public class UDPServerTest {
    public static final int PORT = 8080;
    private DatagramSocket dataSocket;
    private DatagramPacket dataPacket;
    private byte[] receiveByte;

    public UDPServerTest() {
        init();
    }

    public void init() {
        try {
            dataSocket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        receiveByte = new byte[1024];
        dataPacket = new DatagramPacket(receiveByte, receiveByte.length);
    }

    public void run() {
        String receiveStr;
        while (true) {
            try {
                dataSocket.receive(dataPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int i = dataPacket.getLength();
            if (i > 0) {
                // 指定接收到数据的长度,可使接收数据正常显示
                receiveStr = new String(receiveByte, 0, dataPacket.getLength());
                System.out.println(receiveStr);
            }else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String args[]) {
        new UDPServerTest().run();
    }
}
