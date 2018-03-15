import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import software.sigma.nucleoalpha.AlphaNucleoValidator
import software.sigma.nucleobase.actors.{CalculatingActor, Coordinator}
import software.sigma.nucleobase.routing.URLRoute

import scala.concurrent.duration._

class RouteTest  extends WordSpec
  with Matchers
  with ScalaFutures
  with ScalatestRouteTest
  with URLRoute{

  implicit def default(implicit system: ActorSystem) = RouteTestTimeout(5 seconds)

  val myConf: Config = ConfigFactory.load()
  val ID: String = myConf.getString("activeMQ.Validator.ID")
  val url: String = myConf.getString("activeMQ.Validator.url")
  val topicName: String = myConf.getString("activeMQ.Validator.topicName")

  val validator = new AlphaNucleoValidator
  val firstLog = List("abcd")
  val correctModel = validator.buildCorrectModel(firstLog)

  implicit val executionContext = system.dispatcher

  val regulator = system.actorOf(Coordinator.props, "Regulator")
  val rater: ActorRef = system.actorOf(CalculatingActor.props(validator, correctModel, regulator), "Rater")

  lazy val route: Route = getMyRate(rater)

  "route" should {
    "return current rate (GET /rate)" in {

      val request = HttpRequest(uri = "/rate")

      request ~> route ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"rate":0.0}""")
      }
    }
  }
}
