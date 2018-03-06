package software.sigma.nucleobase

import javax.jms.JMSException

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import software.sigma.alpha.AlphaValidator
import ua.com.Subscriber
import ua.com.entity.SchedulingActor


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

    implicit def system: ActorSystem = ActorSystem("ActorSystem")

    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    val rater: ActorRef = system.actorOf(CalculatingActor.props(validator, correctModel), "Rater")
    val batcher: ActorRef = system.actorOf(SchedulingActor.props(rater), "Scheduler")

    while (true) {
      //subscriber.processBatch()
      batcher ! subscriber.processBatch()
      //two actors
    }
  } catch {
    case ex: JMSException => println(ex.getMessage)
  }
}
