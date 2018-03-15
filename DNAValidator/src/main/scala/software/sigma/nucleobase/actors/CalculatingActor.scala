package software.sigma.nucleobase.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import software.sigma.nucleoalpha._
import software.sigma.nucleobase.actors.SchedulingActor.DNALog
import software.sigma.nucleobase.entity.Rate

object CalculatingActor {

  case object RateRequest

  def props(validator: AlphaNucleoValidator, correctModel: List[Place], coordinator: ActorRef): Props = Props(new CalculatingActor(validator, correctModel, coordinator: ActorRef))
}

class CalculatingActor(validator: AlphaNucleoValidator, correctModel: List[Place], coordinator: ActorRef) extends Actor with ActorLogging {

  import CalculatingActor._

  var currentRate: BigDecimal = 0

  override def receive: Receive = {
    case logs: DNALog =>
      val rate = validator.calculateRate(logs.log, correctModel)
      log.info(s"Rate of mutations: $rate")
      coordinator ! Rate(rate)
      currentRate = rate
    case RateRequest =>
      log.info("Received request for rate")
      log.info(s"Current rate of mutations: $currentRate")
      sender() ! Some(Rate(currentRate))
  }
}
