package software.sigma.nucleobase.serializers
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import software.sigma.nucleobase.entity.Rate
import spray.json.DefaultJsonProtocol


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol  {
  implicit val inputURLJsonFormat = jsonFormat1(Rate)
}