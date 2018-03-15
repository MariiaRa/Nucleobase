package software.sigma.nucleobase

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import org.slf4j.LoggerFactory
import software.sigma.nucleobase.actors.Mutator.StartNewTimer
import software.sigma.nucleobase.actors.MutatorCoordinator.ReadMessage
import software.sigma.nucleobase.actors.{Mutator, MutatorCoordinator}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

object DNAMutator extends App {

  private val logger = LoggerFactory.getLogger(this.getClass)

  implicit def system: ActorSystem = ActorSystem("ActorSystem")

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val mutator: ActorRef = system.actorOf(Mutator.props, "Mutator")
  val mutatorCoordinator: ActorRef = system.actorOf(MutatorCoordinator.props(mutator), "CoordinatorOfMutator")

  system.scheduler.scheduleOnce(5.seconds, mutator, StartNewTimer)
  system.scheduler.schedule(0.milliseconds, 2.seconds, mutatorCoordinator, ReadMessage)

}
