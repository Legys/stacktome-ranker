package ranking

import java.util.concurrent.{Executors, TimeUnit}
import scala.util.Try

package object ExecutorService {
  private val scheduledExecutorService = Executors.newScheduledThreadPool(1)
  private val initialDelay = 0

  var repeatInMinutes = 1
  def executeScheduledTask(runnable: Runnable): Unit =
    scheduledExecutorService.scheduleAtFixedRate(
      runnable,
      initialDelay,
      repeatInMinutes,
      TimeUnit.MINUTES
    )
}
