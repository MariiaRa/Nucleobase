package software.sigma.nucleobase

import javax.jms.JMSException
import ua.com.Subscriber

object DNAValidator extends App {

  try {
    val subscriber = new Subscriber("tcp://localhost:61616", "DNA", "DNAValidator")
    // subscriber.receiveMessage()
    // subscriber.processBatch()
    while (true) {
      subscriber.processBatch()
      }
  } catch {
    case ex: JMSException => println(ex.getMessage)
  }
}
