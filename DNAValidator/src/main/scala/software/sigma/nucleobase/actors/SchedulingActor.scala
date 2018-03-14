package software.sigma.nucleobase.actors

import akka.actor._

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

object SchedulingActor {

  case class DNALog(log: List[List[String]])

  case object Key

  case object Tick

  def props(actor: ActorRef): Props = Props(new SchedulingActor(actor))
}

class SchedulingActor(actor: ActorRef) extends Actor with ActorLogging with Timers {

  import SchedulingActor._

  val stream = new ListBuffer[List[String]]

  timers.startPeriodicTimer(Key, Tick, 30.second)

  override def receive: Receive = {
    case batch: String =>
      stream += batch.grouped(2).toList
    case Tick =>
      actor ! DNALog(stream.toList)
     // log.info(s"Size of stream: ${stream.toList.length}")
      stream.clear()
  }
}

