package software.sigma.nucleobase

import java.util.{Timer, TimerTask}

import com.typesafe.config.{Config, ConfigFactory}
import ua.com.entity.Nucleotide._
import ua.com.entity.{DNAGenerator, Nucleotide}
import ua.com.{Publisher, Subscriber}

class MutationPublisher extends Runnable {
  def run {
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
    // publisher.closeConnection()
  }
}

class RateReader extends Runnable {

  override def run(): Unit = {
    val myConf: Config = ConfigFactory.load()
    val raterID: String = myConf.getString("activeMQ.Coordinator.ID")
    val raterUrl: String = myConf.getString("activeMQ.Coordinator.url")
    val raterTopicName: String = myConf.getString("activeMQ.Coordinator.topicName")
    val rateChecker = new Subscriber(raterUrl, raterTopicName, raterID)

    while (true) {
      rateChecker.getRate()
    }
    // publisher.closeConnection()
  }
}

object DNAMutator extends App {

  val thread1 = new Thread(new MutationPublisher)
  thread1.start

  val thread2 = new Thread(new RateReader)
  thread2.start

  /*  private val logger = LoggerFactory.getLogger(this.getClass)
    val generator = new DNAGenerator
    val nucleoStream: Stream[Nucleotide] = generator.mutation(getRandomNucleo)

    val myConf: Config = ConfigFactory.load()
    val mutatorID: String = myConf.getString("activeMQ.DNAMutator.ID")
    val mutatorUrl: String = myConf.getString("activeMQ.DNAMutator.url")
    val mutatorTopicName: String = myConf.getString("activeMQ.DNAMutator.topicName")
    val publisher = new Publisher(mutatorUrl, mutatorTopicName, mutatorID)

    val raterID: String = myConf.getString("activeMQ.Coordinator.ID")
    val raterUrl: String = myConf.getString("activeMQ.Coordinator.url")
    val raterTopicName: String = myConf.getString("activeMQ.Coordinator.topicName")
    val rateChecker = new Subscriber(raterUrl, raterTopicName, raterID)

    logger.info("DMA mutator has started.")
    val task: TimerTask = new TimerTask() {
      def run() {
        nucleoStream take 50 foreach (n => publisher.send(n.nucleo))
      }
    }
    val timer = new Timer()
    val delay = 0
    val intevalPeriod = 15 * 1000

  /*  if (rateChecker.getRate()) timer.scheduleAtFixedRate(task, delay, intevalPeriod)
    else timer.cancel()*/

    timer.scheduleAtFixedRate(task, delay, intevalPeriod)

    // publisher.closeConnection()*/
}
