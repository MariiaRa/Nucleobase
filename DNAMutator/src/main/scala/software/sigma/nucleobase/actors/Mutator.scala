package software.sigma.nucleobase.actors

import java.util.{Timer, TimerTask}
import akka.actor.{Actor, ActorLogging, Props, Timers}
import com.typesafe.config.{Config, ConfigFactory}
import ua.com.Publisher
import ua.com.entity.Nucleotide.getRandomNucleo
import ua.com.entity.{DNAGenerator, Nucleotide}

object Mutator {

  case object Publish

  case object Stop

  case object Key

  def props: Props = Props[Mutator]
}

class Mutator extends Actor with ActorLogging with Timers {

  import Mutator._
  import software.sigma.nucleobase.actors.MutatorCoordinator._

  val generator = new DNAGenerator
  val nucleoStream: Stream[Nucleotide] = generator.mutation(getRandomNucleo)

  val myConf: Config = ConfigFactory.load()
  val mutatorID: String = myConf.getString("activeMQ.DNAMutator.ID")
  val mutatorUrl: String = myConf.getString("activeMQ.DNAMutator.url")
  val mutatorTopicName: String = myConf.getString("activeMQ.DNAMutator.topicName")
  val publisher = new Publisher(mutatorUrl, mutatorTopicName, mutatorID)


  val task: TimerTask = new TimerTask() {
    def run() {
      nucleoStream take 50 foreach (n => publisher.send(n.nucleo))
    }
  }
  val timer = new Timer()
  val delay = 0
  val intevalPeriod = 15 * 1000

  timer.scheduleAtFixedRate(task, delay, intevalPeriod)

  override def receive: Receive = {
    case Publish => log.info("Publishing..."); timer.scheduleAtFixedRate(task, delay, intevalPeriod)
    case Stop => log.info("Stopping..."); timer.cancel(); timer.purge();
    case StartActor => log.info("Starting..."); self ! Publish
  }
}
