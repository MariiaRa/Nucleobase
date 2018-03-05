package software.sigma.nucleobase

import javax.jms.JMSException

import software.sigma.alpha.AlphaValidator
import ua.com.Subscriber


object DNAValidator extends App {
  private def createLog(input: String): List[String] = {
    println(input.grouped(2).toList)
    input.grouped(2).toList
  }

  try {
    val subscriber = new Subscriber("tcp://localhost:61616", "DNA", "DNAValidator")
    val validator = new AlphaValidator
    val firstLog = createLog(subscriber.processBatch())
    val correctModel = validator.buildCorrectModel(firstLog)
    while (true) {
      subscriber.processBatch()
      val log = createLog(subscriber.processBatch())
      println(log)
    val stream: Stream[List[String]] = log #:: Stream.empty[List[String]]
    validator.calculateRate(stream, correctModel)
      println(validator.calculateRate(stream, correctModel))
    }
  } catch {
    case ex: JMSException => println(ex.getMessage)
  }
}
