package software.sigma.nucleobase.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory
import software.sigma.nucleobase.actors.Coordinator.SaveRate
import software.sigma.nucleobase.entity.Rate
import ua.com.Publisher

object Coordinator {
  def props: Props = Props[Coordinator]
  case class SaveRate(rate: Double)
}

class Coordinator extends Actor with ActorLogging {


  private val logger = LoggerFactory.getLogger(this.getClass)

  val myConf: Config = ConfigFactory.load()
  val ID: String = myConf.getString("activeMQ.Coordinator.ID")
  val url: String = myConf.getString("activeMQ.Coordinator.url")
  val topicName: String = myConf.getString("activeMQ.Coordinator.topicName")

  val regulator = new Publisher(url, topicName, ID)
  logger.info("Service coordinator has started.")

  var previousRate = 0.0

  override def receive: Receive = {
    case r: Rate =>
      self ! SaveRate(r.rate)
      if (r.rate > 0.01 && previousRate < 0.01) {
        regulator.send("Stop")
      } else if (r.rate < 0.01 && previousRate > 0.01){
        regulator.send("Start")
      }
    case save: SaveRate =>
      previousRate = save.rate
  }
}
