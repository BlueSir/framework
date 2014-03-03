package com.sohu.smc.litecore.http

import java.net.InetSocketAddress
import java.util.concurrent.{Executors, ExecutorService, TimeUnit}
import java.util.concurrent.atomic.AtomicInteger
import com.twitter.logging.Logger
import com.twitter.ostrich.admin.{RuntimeEnvironment, Service}
import com.twitter.ostrich.stats.Stats
import com.twitter.util.{Duration, Time, Timer => TTimer, TimerTask => TTimerTask}
import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.channel.{Channel, ChannelFactory, ChannelPipelineFactory, Channels}
import org.jboss.netty.channel.group.DefaultChannelGroup
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.util.{HashedWheelTimer, Timer}
import com.twitter.ostrich.admin.config.{TimeSeriesCollectorConfig, StatsConfig, AdminServiceConfig}
import org.jboss.netty.handler.codec.http.{HttpChunkAggregator, HttpRequestDecoder, HttpResponseEncoder, HttpContentCompressor}
import com.sohu.smc.litecore.Controller

class WebService(listenAddress: String, __httpListenPort: Int,
                 clientTimeout: Option[Duration],
                 httpController: Controller)
  extends Service {
  private val log = Logger.get(getClass.getName)

  var timer: Timer = null
  var executor: ExecutorService = null
  var channelFactory: ChannelFactory = null
  var httpAcceptor: Option[Channel] = None
  val channelGroup = new DefaultChannelGroup("channels")
  val httpListenPort: Option[Int] = toOption(__httpListenPort)

  def toOption[T](value: T): Option[T] = if (value == null) None else Some(value)

  private def bytesRead(n: Int) {
    Stats.incr("bytes_read", n)
  }

  private def bytesWritten(n: Int) {
    Stats.incr("bytes_written", n)
  }

  def start() {
    log.info("WebService config: listenAddress=%s httpPort=%s " +
      "clientTimeout=%s",
      listenAddress, httpListenPort, clientTimeout)

    // this means no timeout will be at better granularity than 10ms.
    timer = new HashedWheelTimer(10, TimeUnit.MILLISECONDS)

    // netty setup:
    executor = Executors.newCachedThreadPool()
    channelFactory = new NioServerSocketChannelFactory(executor, executor)


    val httpPipelineFactory = new ChannelPipelineFactory() {
      def getPipeline() = {
        val handler = new HttpHandler(channelGroup, httpController, clientTimeout)
        val pipeline = Channels.pipeline()

        pipeline.addLast("decoder", new HttpRequestDecoder)
        pipeline.addLast("aggregator", new HttpChunkAggregator(128 * 1000));
        pipeline.addLast("encoder", new HttpResponseEncoder)
        pipeline.addLast("deflater", new HttpContentCompressor)
        pipeline.addLast("handler", handler)

        pipeline
        //Channels.pipeline(protocolCodec, handler)
      }
    }
    httpAcceptor = httpListenPort.map {
      port =>
        val address = new InetSocketAddress(listenAddress, port)
        makeAcceptor(channelFactory, httpPipelineFactory, address)
    }
    // optionally, start a periodic timer to clean out expired items.
    /*if (expirationTimerFrequency.isDefined) {
      log.info("Starting up background expiration task.")
      new PeriodicBackgroundProcess("background-expiration", expirationTimerFrequency.get) {
        def periodic() {
          //
        }
      }.start()
    }*/
  }

  def shutdown() {
    log.info("Shutting down!")

    httpAcceptor.foreach {
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
    /*try {
      log.info("Reloading %s ...", Kestrel.runtime.configFile)
      new Eval().apply[KestrelConfig](Kestrel.runtime.configFile).reload(this)
    } catch {
      case e: Eval.CompilerException =>
        log.error(e, "Error in config: %s", e)
        log.error(e.messages.flatten.mkString("\n"))
    }*/
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

object WebService {
  val log = Logger.get(getClass.getName)
  var webservice: WebService = null
  var runtime: RuntimeEnvironment = null

  private val startTime = Time.now

  // track concurrent sessions
  val sessions = new AtomicInteger()
  val sessionId = new AtomicInteger()

  def main(args: Array[String]): Unit = {
    try {
      runtime = RuntimeEnvironment(this, args)
      webservice = runtime.loadRuntimeConfig[WebService]()

      Stats.addGauge("connections") {
        sessions.get().toDouble
      }

      webservice.start()
    } catch {
      case e =>
        log.error(e, "Exception during startup; exiting!")
        System.exit(1)
    }
    log.info("WebService %s started.", runtime.jarVersion)
  }

  def makeServer(args: Array[String], listenAddress: String, httpListenPort: Int, __adminListenPort: Int,
                 clientTimeout: Option[Duration],
                 httpController: Controller) = {
    try {
      var webservice: WebService = null

      Stats.addGauge("connections") {
        sessions.get().toDouble
      }

      webservice = new WebService(listenAddress, httpListenPort, clientTimeout, httpController)

      webservice.start()

      runtime = RuntimeEnvironment(this, Array())
      def toOption[T](value: T): Option[T] = if (value == null) None else Some(value)
      val adminListenPort: Option[Int] = toOption(__adminListenPort)

      if (adminListenPort.isDefined) {
        val adminConfig = new AdminServiceConfig {
          httpPort = adminListenPort
          statsNodes = new StatsConfig {
            reporters = new TimeSeriesCollectorConfig
          }
        }
        val admin = adminConfig()(runtime)
      }
    } catch {
      case e =>
        log.error(e, "Exception during startup; exiting!")
        System.exit(1)
    }
    log.info("Server %s started.", runtime.jarVersion)
  }

  def uptime() = Time.now - startTime
}