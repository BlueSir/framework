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

package com.sohu.smc.litecore.memcached

import java.net.InetSocketAddress
import java.util.concurrent.{Executors, ExecutorService, TimeUnit}
import java.util.concurrent.atomic.AtomicInteger
import com.twitter.logging.Logger
import com.twitter.naggati.codec.MemcacheCodec
import com.twitter.ostrich.admin.{RuntimeEnvironment, Service}
import com.twitter.ostrich.stats.Stats
import com.twitter.util.{Duration, Time, Timer => TTimer, TimerTask => TTimerTask}
import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.channel.{Channel, ChannelFactory, ChannelPipelineFactory, Channels}
import org.jboss.netty.channel.group.DefaultChannelGroup
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.util.{HashedWheelTimer, Timeout, Timer, TimerTask}
import com.sohu.smc.litecore.HandlerFunction

// FIXME move me!
class NettyTimer(underlying: Timer) extends TTimer {
  def schedule(when: Time)(f: => Unit): TTimerTask = {
    val timeout = underlying.newTimeout(new TimerTask {
      def run(to: Timeout) {
        if (!to.isCancelled) f
      }
    }, (when - Time.now).inMilliseconds max 0, TimeUnit.MILLISECONDS)
    toTimerTask(timeout)
  }

  def schedule(when: Time, period: Duration)(f: => Unit): TTimerTask = {
    val task = schedule(when) {
      f
      schedule(when + period, period)(f)
    }
    task
  }

  def stop() {
    underlying.stop()
  }

  private[this] def toTimerTask(task: Timeout) = new TTimerTask {
    def cancel() {
      task.cancel()
    }
  }
}


class MemcachedService(memAction: HandlerFunction,
                       listenAddress: String, _memcacheListenPort: Int,
                       clientTimeout: Option[Duration],
                       maxOpenTransactions: Int)
  extends Service {
  private val log = Logger.get(getClass.getName)

  val memcacheListenPort: Option[Int] = toOption(_memcacheListenPort)

  def toOption[T](value: T): Option[T] = if (value == null) None else Some(value)

  var timer: Timer = null
  var executor: ExecutorService = null
  var channelFactory: ChannelFactory = null
  var memcacheAcceptor: Option[Channel] = None
  val channelGroup = new DefaultChannelGroup("channels")

  private def bytesRead(n: Int) {
    Stats.incr("bytes_read", n)
  }

  private def bytesWritten(n: Int) {
    Stats.incr("bytes_written", n)
  }

  def start() {
    log.info("Kestrel config: listenAddress=%s memcachePort=%s " +
      "clientTimeout=%s maxOpenTransactions=%d",
      listenAddress, memcacheListenPort,
      clientTimeout, maxOpenTransactions)

    // this means no timeout will be at better granularity than 10ms.
    timer = new HashedWheelTimer(10, TimeUnit.MILLISECONDS)

    // netty setup:
    executor = Executors.newCachedThreadPool()
    channelFactory = new NioServerSocketChannelFactory(executor, executor)

    val memcachePipelineFactory = new ChannelPipelineFactory() {
      def getPipeline() = {
        val protocolCodec = MemcacheCodec.asciiCodec(bytesRead, bytesWritten)

        val handler = new MemcacheHandler(channelGroup, maxOpenTransactions, clientTimeout, memAction)
        Channels.pipeline(protocolCodec, handler)
      }
    }
    memcacheAcceptor = memcacheListenPort.map {
      port =>
        val address = new InetSocketAddress(listenAddress, port)
        makeAcceptor(channelFactory, memcachePipelineFactory, address)
    }


  }

  def shutdown() {
    log.info("Shutting down!")

    memcacheAcceptor.foreach {
      _.close().awaitUninterruptibly()
    }
    channelGroup.close().awaitUninterruptibly()
    channelFactory.releaseExternalResources()

    executor.shutdown()
    executor.awaitTermination(5, TimeUnit.SECONDS)
    timer.stop()
    timer = null
    log.info("Goodbye.")
  }

  override def reload() {

  }


  private def makeAcceptor(channelFactory: ChannelFactory, pipelineFactory: ChannelPipelineFactory,
                           address: InetSocketAddress): Channel = {
    val bootstrap = new ServerBootstrap(channelFactory)
    bootstrap.setPipelineFactory(pipelineFactory)
    bootstrap.setOption("backlog", 1000)
    bootstrap.setOption("reuseAddress", true)
    bootstrap.setOption("child.keepAlive", true)
    bootstrap.setOption("child.tcpNoDelay", true)
    bootstrap.bind(address)
  }
}

object MemcachedService {
  val log = Logger.get(getClass.getName)
  var kestrel: MemcachedService = null
  var runtime: RuntimeEnvironment = null

  private val startTime = Time.now
  runtime = RuntimeEnvironment(this, Array())
  // track concurrent sessions
  val sessions = new AtomicInteger()
  val sessionId = new AtomicInteger()

  def mkServer(
               listenAddress: String, memcacheListenPort: Int,
               clientTimeout: Option[Duration],
               maxOpenTransactions: Int,
               memAction: HandlerFunction) {
    kestrel = new MemcachedService(memAction, listenAddress, memcacheListenPort, clientTimeout, maxOpenTransactions)
    kestrel.start();
  }

  def main(args: Array[String]): Unit = {
    try {
      runtime = RuntimeEnvironment(this, args)
      kestrel = runtime.loadRuntimeConfig[MemcachedService]()

      Stats.addGauge("connections") {
        sessions.get().toDouble
      }

      kestrel.start()
    } catch {
      case e =>
        log.error(e, "Exception during startup; exiting!")
        System.exit(1)
    }
    log.info("Kestrel %s started.", runtime.jarVersion)
  }

  def uptime() = Time.now - startTime
}
