package software.sigma.nucleobase

import javax.jms.JMSException

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory
import ua.com.Publisher
import ua.com.entity.Nucleotide._
import ua.com.entity.{DNAGenerator, Nucleotide}

object DNAProducer extends App {
  private val logger = LoggerFactory.getLogger(this.getClass)
  try {
    val generator = new DNAGenerator
    val DNAStream: Stream[Nucleotide] = generator.nucleo(getRandomNucleo, false)

    val myConf: Config = ConfigFactory.load()
    val ID: String = myConf.getString("DNAProducer.ID")
    val url: String = myConf.getString("DNAProducer.url")
    val topicName: String = myConf.getString("DNAProducer.topicName")

    val publisher = new Publisher(url, topicName, ID)
    logger.info("DNA producer has started.")
    while (true) {
      DNAStream take 1000 foreach (n => publisher.send(n.nucleo))
    }
    publisher.closeConnection()
  } catch {
    case ex: JMSException => println(ex.getMessage)
  }
}
