package software.sigma.nucleobase

import java.util.{Timer, TimerTask}
import javax.jms.JMSException

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory
import ua.com.Publisher
import ua.com.entity.NucleotideTransition._
import ua.com.entity.{DNAGenerator, Nucleotides}
import scala.concurrent.duration._

object DNAProducer extends App {
  private val logger = LoggerFactory.getLogger(this.getClass)
  try {
    val COUNT: Int = 2000
    val generator = new DNAGenerator
    val DNAStream: Stream[Nucleotides] = generator.buildDNA(getRandomNucleo, false)

    val myConf: Config = ConfigFactory.load()
    val ID: String = myConf.getString("DNAProducer.ID")
    val url: String = myConf.getString("DNAProducer.url")
    val topicName: String = myConf.getString("DNAProducer.topicName")

    val publisher = new Publisher(url, topicName, ID)
    logger.info("DNA producer has started.")

    val task: TimerTask = new TimerTask() {
      def run() {
        DNAStream take COUNT foreach (nucleobase => publisher.send(nucleobase.nucleo))
      }
    }
    val timer = new Timer()
    val delay = 0
    val interval = 5.seconds

    timer.scheduleAtFixedRate(task, delay, interval.length)

  } catch {
    case ex: JMSException => logger.error(ex.getMessage)
  }
}
