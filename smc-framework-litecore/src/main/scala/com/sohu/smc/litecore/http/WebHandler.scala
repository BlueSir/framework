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
import com.twitter.logging.Logger
import com.twitter.ostrich.admin.{BackgroundProcess, ServiceTracker}
import com.twitter.ostrich.stats.Stats
import java.util.concurrent.atomic.AtomicBoolean
import com.sohu.smc.litecore.Controller

class TooManyOpenTransactionsException extends Exception("Too many open transactions.")
object TooManyOpenTransactionsException extends TooManyOpenTransactionsException

/**
 * Common implementations of kestrel commands that don't depend on which protocol you're using.
 */
abstract class NettyHandler(httpController: Controller) {
  private val log = Logger.get(getClass.getName)

  val sessionId = WebService.sessionId.incrementAndGet()
  val finished = new AtomicBoolean(false)

  WebService.sessions.incrementAndGet()
  Stats.incr("total_connections")

  protected def clientDescription: String

  // usually called when netty sends a disconnect signal.
  protected def finish() {
    if (finished.getAndSet(true) == false) {
      log.debug("End of session %d", sessionId)
      WebService.sessions.decrementAndGet()
    }
  }

  protected def shutdown() = {
    BackgroundProcess {
      Thread.sleep(100)
      ServiceTracker.shutdown()
    }
  }
}
