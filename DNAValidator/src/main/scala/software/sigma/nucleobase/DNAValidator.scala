package software.sigma.nucleobase

import javax.jms.JMSException

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import org.slf4j.LoggerFactory
import software.sigma.nucleoalpha._
import software.sigma.nucleobase.actors.{CalculatingActor, SchedulingActor}
import ua.com.Subscriber

object DNAValidator extends App {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private def createLog(input: String): List[String] = {
    input.grouped(2).toList
  }

  try {
    val subscriber = new Subscriber("tcp://localhost:61616", "DNA", "DNAValidator")
    val validator = new AlphaNucleoValidator
    val firstLog = createLog(subscriber.processBatch())
    val correctModel = validator.buildCorrectModel(firstLog)

    implicit def system: ActorSystem = ActorSystem("ActorSystem")

    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    val rater: ActorRef = system.actorOf(CalculatingActor.props(validator, correctModel), "Rater")
    val batcher: ActorRef = system.actorOf(SchedulingActor.props(rater), "Scheduler")
    logger.info("Validator has started.")
    while (true) {
      batcher ! subscriber.processBatch()
    }
  } catch {
    case ex: JMSException => println(ex.getMessage)
  }
}
