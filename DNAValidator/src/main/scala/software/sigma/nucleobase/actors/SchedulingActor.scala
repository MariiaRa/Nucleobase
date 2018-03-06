package software.sigma.nucleobase.actors

import akka.actor._

import scala.concurrent.duration._

object SchedulingActor {

  case class DNALog(log: Stream[List[String]])

  case object SendLogs

  case object Key

  case object Tick

  def props(actor: ActorRef): Props = Props(new SchedulingActor(actor))
}


class SchedulingActor(actor: ActorRef) extends Actor with ActorLogging with Timers {

  import SchedulingActor._

  lazy val stream = Stream.empty[List[String]]

  timers.startPeriodicTimer(Key, Tick, 1.second)

  override def receive: Receive = {
    case batch: String =>
      batch.grouped(2).toList #:: stream
    case Tick =>
      log.info(s"size of stream: ${stream.size}")
      actor ! stream
  }
}

