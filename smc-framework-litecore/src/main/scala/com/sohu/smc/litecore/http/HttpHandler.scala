package com.sohu.smc.litecore.http

/*
 * Copyright 2009 Twitter, Inc.
 * Copyright 2009 Robey Pointer <robeypointer@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jboss.netty.handler.codec.http.DefaultHttpResponse
import org.jboss.netty.handler.codec.http.HttpHeaders
import org.jboss.netty.handler.codec.http.HttpHeaders.Names
import org.jboss.netty.handler.codec.http.HttpRequest
import org.jboss.netty.handler.codec.http.HttpResponse
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import org.jboss.netty.handler.codec.http.HttpVersion
import org.jboss.netty.util.CharsetUtil
import com.twitter.util.Duration
import org.jboss.netty.channel.group.ChannelGroup
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.channel.ChannelFutureListener
import com.sohu.smc.litecore.Controller

/**
 * Memcache protocol handler for a kestrel connection.
 */
class HttpHandler(
                   channelGroup: ChannelGroup,
                   httpController: Controller,
                   clientTimeout: Option[Duration])
  extends NettyHttpHandler[HttpRequest](channelGroup, httpController, clientTimeout) {
  protected final def handle(request: HttpRequest) = {
    //System.out.print(clientDescription);

    val buf: StringBuilder = new StringBuilder
    val response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
    response.setHeader(Names.CONTENT_TYPE, "text/plain; charset=UTF-8")
    request.setHeader("remoteAddr",clientDescription)
    try{
      buf.append(httpController.doAction(request,response))
    } catch {
      case e =>
        e.printStackTrace()
        buf.append("{\"errno\":-99,\"data\":\"\",\"errText\":\"服务异常\"}")
    }

    writeResponse(request, response, buf)
  }

  private def writeResponse(request: HttpRequest,response: HttpResponse, buf: StringBuilder): Unit = {
    val keepAlive = HttpHeaders.isKeepAlive(request)

    response.setContent(ChannelBuffers.copiedBuffer(buf.toString, CharsetUtil.UTF_8))

    if (keepAlive) response.setHeader(Names.CONTENT_LENGTH, response.getContent.readableBytes)

    /*request.getHeader(Names.COOKIE) match {
      case null =>
      case cookieString =>
        val cookieDecoder = new CookieDecoder
        val cookies = cookieDecoder.decode(cookieString)

        if (!cookies.isEmpty) {
          val cookieEncoder = new CookieEncoder(true)
          //for (cookie <- cookies) cookieEncoder.addCookie(cookie)
          response.addHeader(Names.SET_COOKIE, cookieEncoder.encode)
        }
    }*/

    val future = channel.write(response)

    if (!keepAlive) future.addListener(ChannelFutureListener.CLOSE)
  }


  protected final def handleProtocolError() {
    //channel.write(new MemcacheResponse("CLIENT_ERROR"))
  }

  protected final def handleException(e: Throwable) {
    //channel.write(new MemcacheResponse("ERROR"))
  }

  private def quit() = {
    channel.close()
  }
}
