package software.sigma.nucleobase

import javax.jms.JMSException

import org.slf4j.LoggerFactory
import ua.com.Publisher
import ua.com.entity.Nucleotide._
import ua.com.entity.{DNAGenerator, Nucleotide}

object DNAProducer extends App {
  private val logger = LoggerFactory.getLogger(this.getClass)
  try {
    logger.info("DNA producer has started.")
    val generator = new DNAGenerator
    val DNAStream: Stream[Nucleotide] = generator.nucleo(getRandomNucleo, false)
    val publisher = new Publisher

    while (true) {
      DNAStream take 1000 foreach (n => publisher.send(n.nucleo))
    }

    publisher.closeConnection()
  } catch {
    case ex: JMSException => println(ex.getMessage)
  }

}
