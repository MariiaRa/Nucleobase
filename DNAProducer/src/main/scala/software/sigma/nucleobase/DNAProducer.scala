package software.sigma.nucleobase

import javax.jms.JMSException
import ua.com.Publisher
import ua.com.entity.Nucleotide._
import ua.com.entity.{DNAGenerator, Nucleotide}

object DNAProducer extends App {

  try {
    val generator = new DNAGenerator
    val DNAStream: Stream[Nucleotide] = generator.nucleo(getRandomNucleo, false)
    val publisher = new Publisher("tcp://localhost:61616", "DNA", "DNAProducer")
//   while(true) {
     DNAStream take 400 foreach (n => publisher.send(n.nucleo))
 //  }
    //publisher.send(n.nucleo)
    publisher.closeConnection()
  } catch {
    case ex: JMSException => println(ex.getMessage)
  }

}
