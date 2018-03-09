package software.sigma.nucleobase

import javax.jms.JMSException

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory
import software.sigma.nucleoalpha._
import software.sigma.nucleobase.actors.{CalculatingActor, Coordinator, SchedulingActor}
import software.sigma.nucleobase.routing.URLRoute
import ua.com.Subscriber

import scala.io.StdIn

object DNAValidator extends App with URLRoute {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private def createLog(input: String): List[String] = {
    input.grouped(2).toList
  }

  try {
    val myConf: Config = ConfigFactory.load()
    val ID: String = myConf.getString("activeMQ.Validator.ID")
    val url: String = myConf.getString("activeMQ.Validator.url")
    val topicName: String = myConf.getString("activeMQ.Validator.topicName")

    val subscriber = new Subscriber(url, topicName, ID)

    val validator = new AlphaNucleoValidator
    val firstLog = createLog(subscriber.processBatch())
    val correctModel = validator.buildCorrectModel(firstLog)

    implicit def system: ActorSystem = ActorSystem("ActorSystem")

    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val regulator = system.actorOf(Coordinator.props, "Regulator")
    val rater: ActorRef = system.actorOf(CalculatingActor.props(validator, correctModel, regulator), "Rater")
    val batcher: ActorRef = system.actorOf(SchedulingActor.props(rater), "Scheduler")

    lazy val route: Route = getMyRate(rater)

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    Console.println("Server online at http://localhost:8080/\nPress RETURN to stop...")

    logger.info("Validator has started.")

    while (true) {
      batcher ! subscriber.processBatch()
    }

    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  } catch {
    case ex: JMSException => println(ex.getMessage)
  }
}
