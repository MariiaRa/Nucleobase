package software.sigma.nucleobase

import java.util.{Timer, TimerTask}
import javax.jms.JMSException

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory
import ua.com.Publisher
import ua.com.entity.NucleotideTransition._
import ua.com.entity.{DNAGenerator, Nucleotides}

object DNAProducer extends App {
  private val logger = LoggerFactory.getLogger(this.getClass)
  try {
    val generator = new DNAGenerator
    val DNAStream: Stream[Nucleotides] = generator.nucleo(getRandomNucleo, false)

    val myConf: Config = ConfigFactory.load()
    val ID: String = myConf.getString("DNAProducer.ID")
    val url: String = myConf.getString("DNAProducer.url")
    val topicName: String = myConf.getString("DNAProducer.topicName")

    val publisher = new Publisher(url, topicName, ID)
    logger.info("DNA producer has started.")

    val task: TimerTask = new TimerTask() {
      def run() {
        DNAStream take 40000 foreach (n => publisher.send(n.nucleo))
      }
    }
    val timer = new Timer()
    val delay = 0
    val intevalPeriod = 10 * 1000

    timer.scheduleAtFixedRate(task, delay, intevalPeriod)

    /*while (true) {
      DNAStream take 10000 foreach (n => publisher.send(n.nucleo))
    }*/
    //  publisher.closeConnection()
  } catch {
    case ex: JMSException => println(ex.getMessage)
  }
}
