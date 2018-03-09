package software.sigma.nucleobase.routing

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.onSuccess
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import org.slf4j.LoggerFactory
import software.sigma.nucleobase.actors.CalculatingActor.RateRequest
import software.sigma.nucleobase.entity.Rate
import software.sigma.nucleobase.serializers.JsonSupport

import scala.concurrent.Future
import scala.concurrent.duration._

trait URLRoute extends JsonSupport {

  implicit def system: ActorSystem = ActorSystem("ActorSystem")

  implicit lazy val timeout: Timeout = Timeout(5.seconds)
  private val logger = LoggerFactory.getLogger("Route logger")

  def getMyRate(rater: ActorRef): Route = {

    val myRoutes =
      get {
        path("rate") {

          val rate: Future[Option[Rate]] = (rater ? RateRequest).mapTo[Option[Rate]]
          onSuccess(rate) {
            case Some(currentRate) => complete(currentRate)
            case None => complete(StatusCodes.NotFound)
          }
        }
      }

    myRoutes
  }
}
