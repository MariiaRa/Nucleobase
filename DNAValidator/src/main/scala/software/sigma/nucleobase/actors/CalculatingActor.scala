package software.sigma.nucleobase.actors

import akka.actor.{Actor, ActorLogging, Props}
import software.sigma.alpha.{AlphaValidator, Place}
import software.sigma.nucleobase.actors.SchedulingActor.DNALog

object CalculatingActor {
  def props(validator: AlphaValidator, correctModel: List[Place]): Props = Props(new CalculatingActor(validator, correctModel))
}

class CalculatingActor (validator: AlphaValidator, correctModel: List[Place]) extends Actor with ActorLogging{
  override def receive: Receive = {
    case logs: DNALog =>
      validator.calculateRate(logs.log,correctModel)
  }
}
