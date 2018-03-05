package software.sigma.nucleobase

import javax.jms.JMSException
import ua.com.Subscriber

object DNAValidator extends App {
  private def createLog(input: String): List[String] = {
    println(input.grouped(2).toList)
    input.grouped(2).toList
  }

  try {
    val subscriber = new Subscriber("tcp://localhost:61616", "DNA", "DNAValidator")

    while (true) {
      subscriber.processBatch()
     }
  } catch {
    case ex: JMSException => println(ex.getMessage)
  }
}
