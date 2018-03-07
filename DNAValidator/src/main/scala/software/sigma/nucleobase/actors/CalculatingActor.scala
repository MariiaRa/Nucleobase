package software.sigma.nucleobase.actors

import akka.actor.{Actor, ActorLogging, Props}
import software.sigma.nucleoalpha._
import software.sigma.nucleobase.actors.SchedulingActor.DNALog

object CalculatingActor {
  def props(validator: AlphaNucleoValidator, correctModel: List[Place]): Props = Props(new CalculatingActor(validator, correctModel))
}

class CalculatingActor(validator: AlphaNucleoValidator, correctModel: List[Place]) extends Actor with ActorLogging {
  override def receive: Receive = {
    case logs: DNALog =>
      val rate = validator.calculateRate(logs.log, correctModel)
      log.info(s"Rate of mutations: $rate")
  }
}
