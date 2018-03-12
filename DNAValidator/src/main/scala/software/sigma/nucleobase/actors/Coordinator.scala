package software.sigma.nucleobase.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory
import software.sigma.nucleobase.entity.Rate
import ua.com.Publisher

object Coordinator {
  def props: Props = Props[Coordinator]
}

class Coordinator extends Actor with ActorLogging {
  private val logger = LoggerFactory.getLogger(this.getClass)

  val myConf: Config = ConfigFactory.load()
  val ID: String = myConf.getString("activeMQ.Coordinator.ID")
  val url: String = myConf.getString("activeMQ.Coordinator.url")
  val topicName: String = myConf.getString("activeMQ.Coordinator.topicName")

  val regulator = new Publisher(url, topicName, ID)
  logger.info("Service coordinator has started.")

  override def receive: Receive = {
    case r: Rate =>
     // logger.info("DNA producer has started.")
      if (r.rate > 0.01) {
        regulator.send("Stop")
      } else {
        regulator.send("Start")
      }
  }
}
