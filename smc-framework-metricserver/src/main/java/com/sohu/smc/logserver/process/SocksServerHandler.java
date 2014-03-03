/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.sohu.smc.logserver.process;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;

import java.nio.charset.Charset;


@ChannelHandler.Sharable
public final class SocksServerHandler extends ChannelInboundMessageHandlerAdapter<DatagramPacket> {
    private static final String name = "socket_server_handler";

    public static String getName() {
        return name;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, DatagramPacket data) throws Exception {

        ByteBuf buf = data.data();
        byte[] b = new byte[buf.readableBytes()];
        buf.readBytes(b);
        System.out.println(new String(b, Charset.defaultCharset()));
        ctx.write(buf);
        ctx.flush();
//        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) throws Exception {
        throwable.printStackTrace();
        ctx.channel().close();
    }
}
