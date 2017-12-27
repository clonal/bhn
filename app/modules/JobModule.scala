package modules

import jobs.{AuthTokenCleaner, SendEmailWithNextToken}
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
 * The job module.
 */
class JobModule extends ScalaModule with AkkaGuiceSupport {

  /**
   * Configures the module.
   */
  def configure() = {
    bindActor[SendEmailWithNextToken]("send-email-with-next")
//    bind[Scheduler].asEagerSingleton()
  }
}
