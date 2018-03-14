package software.sigma.nucleobase.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.typesafe.config.{Config, ConfigFactory}
import software.sigma.nucleobase.actors.Mutator.Stop
import ua.com.Subscriber

object MutatorCoordinator {

  case object ReadMessage

  case object StartActor

  def props(actor: ActorRef): Props = Props(new MutatorCoordinator(actor))
}

class MutatorCoordinator(actor: ActorRef) extends Actor with ActorLogging {

  import MutatorCoordinator._

  val myConf: Config = ConfigFactory.load()
  val raterID: String = myConf.getString("activeMQ.Coordinator.ID")
  val raterUrl: String = myConf.getString("activeMQ.Coordinator.url")
  val raterTopicName: String = myConf.getString("activeMQ.Coordinator.topicName")
  val rateChecker = new Subscriber(raterUrl, raterTopicName, raterID)

  override def receive: Receive = {
    case ReadMessage => {
      rateChecker.getCommand() match {
        case Some("Stop") => actor ! Stop
        case Some("Start") => actor ! StartActor
        case None =>
      }
    }
  }
}
