package software.sigma.nucleobase.actors

import akka.actor.{Actor, ActorLogging, Props, Timers}
import com.typesafe.config.{Config, ConfigFactory}
import ua.com.Publisher
import ua.com.entity.NucleotideTransition._
import ua.com.entity.{DNAGenerator, Nucleotides}

import scala.concurrent.duration._

object Mutator {

  case object Publish

  case object Stop

  case object Key

  case object StartNewTimer

  def props: Props = Props[Mutator]
}

class Mutator extends Actor with ActorLogging with Timers {

  import Mutator._
  import software.sigma.nucleobase.actors.MutatorCoordinator._

  val generator = new DNAGenerator
  val nucleoStream: Stream[Nucleotides] = generator.mutation(getRandomNucleo)

  val myConf: Config = ConfigFactory.load()
  val mutatorID: String = myConf.getString("activeMQ.DNAMutator.ID")
  val mutatorUrl: String = myConf.getString("activeMQ.DNAMutator.url")
  val mutatorTopicName: String = myConf.getString("activeMQ.DNAMutator.topicName")
  val publisher = new Publisher(mutatorUrl, mutatorTopicName, mutatorID)

  override def receive: Receive = {
    case StartNewTimer =>
      log.info("Starting timer...")
      timers.startPeriodicTimer(Key, Publish, 14 seconds)
    case Publish =>
      log.info("Publishing...")
      nucleoStream take 2 foreach (n => publisher.send(n.nucleo))
    case Stop =>
      log.info("Stopping...")
      timers.cancelAll()
    case StartActor =>
      log.info("Restarting...")
      self ! StartNewTimer
  }
}
