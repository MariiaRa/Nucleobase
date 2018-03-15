package software.sigma.alpha

import scala.collection.mutable.ListBuffer

class AlphaAlgorithm(eventLog: List[String]) {

  private val footprint = new FootprintMartix(eventLog)
  private val directFollowers = footprint.getDirectFollowers(eventLog)
  private val causality = footprint.getCausalities(directFollowers)
  private val parallels = footprint.getParallelism(directFollowers)
  private val choices = footprint.getExclusiveness(directFollowers)
  footprint.buildRelations(causality, parallels, choices, directFollowers)

  /**
    * 1 step: get Tw - list of distinct events in W (workflow log)
    *
    * @param eventLog a list of traces from where the events are extracted
    * @return list of all seen events in event log
    */
  private def getAllSeenEvents(eventLog: List[String]): List[Char] = {
    val seenEvents = for (
      trace <- eventLog;
      event <- trace) yield event
    seenEvents.distinct
  }

  /**
    * 2 step: get Ti - list of start events - first element in each trace in W
    *
    * @param eventLog a list of traces from where the events are extracted
    * @return sorted list of all initial events in each trace in W
    */
  def initialEvents(eventLog: List[String]): List[Char] = {
    val ti = for (pair <- eventLog) yield pair(0)
    ti.distinct.sortWith(_ < _)
  }

  /**
    * 3 step: get To - list of end events, last element in each trace in W
    *
    * @param eventLog a list of traces from where the events are extracted
    * @return sorted list of all end events in each trace in W
    */
  def endEvents(eventLog: List[String]): List[Char] = {
    val to = for (pair <- eventLog) yield pair(pair.length - 1)
    to.distinct.sortWith(_ < _)
  }

  /**
    * 4 step: generate list of (A,B) where a in A and b in B are in causality relation, all events in A have independent relations and same for B
    *
    * @param causality a list of event pairs which have relation type of causality
    * @return list of a and b which have relation type of causality
    **/
  def makeXL(causality: List[(Char, Char)]): List[(List[Char], List[Char])] = {

    // all input events in causality relations
    val inputEvents = causality.map(a => a._1).distinct
    // all output events in causality relations
    val outputEvents = causality.map(a => a._2).distinct

    // superlist of all possible combinations of a from A
    val inputEventsSuperList = (1 until inputEvents.size).flatMap(inputEvents.toList.combinations).map(_.toList).toList
    //  superlist of all possible combinations of b from B
    val outputEventsSuperList = (1 until outputEvents.size).flatMap(outputEvents.toList.combinations).map(_.toList).toList

    /**
      * retrieve a type of relations from footprint matrix
      *
      * @param firstEvent  - input event
      * @param secondEvent - output event
      * @return type of relation (direct following, causality, parallelism or no relations)
      */

    def getRelationType(firstEvent: Char, secondEvent: Char): String = {
      val rowIndex: Int = footprint.matrixEventToIndex(firstEvent)
      val colIndex: Int = footprint.matrixEventToIndex(secondEvent)
      footprint.matrix(rowIndex)(colIndex)
    }

    /**
      * check if events from given group have any relations
      *
      * @param events - group of events from list of all possible combinations of a events from A or b events from B
      * @return list of booleans indicating whether events are connected or not
      */

    def checkConnected(events: List[Char]): List[Boolean] = {
      val booleanList = for {
        i ← events.indices
        n1 = events(i)
        j ← events.indices
        n2 = events(j)
      } yield getRelationType(n1, n2) != "#"
      booleanList.distinct.toList
    }

    /**
      * firstEvent - a
      * outEvent - b
      * check if all a events in A have independent relations
      * check if all b events in B have independent relations
      *
      * pass superlist of all possible combinations of a from A or b from B
      * check whether connected events are present in each combination from superlist
      * if yes (list contains "true") - discard this group of connected events, keep only all a from A and b from B with independent relations
      *
      * @param events list of all possible combinations of a in A or b in B
      * @return list of a/b events that are independent
      */
    def getIndependentEvents(events: List[List[Char]]) = {
      val eventList = for {
        event <- events
        if !checkConnected(event).contains(true)
      } yield event
      eventList.distinct
    }

    /**
      * check if a and b events have any relations
      *
      * @param inEvent  a from A
      * @param outEvent b from B
      * @return list of booleans indicating whether events are connected or not
      */

    def checkABConnected(inEvent: List[Char], outEvent: List[Char]): List[Boolean] = {
      val t = for {
        a <- inEvent
        b <- outEvent
      } yield getRelationType(a, b) == "->"
      t.distinct
    }

    /**
      * For every a in A and b in B => a -> b
      *
      * @param inEvents  list of a events from A that are independent
      * @param outEvents list of b events from B that are independent
      * @return list of a and b which have relation type of causality
      */
    def findABPairs(inEvents: List[List[Char]], outEvents: List[List[Char]]) = {
      val x = for {
        a <- inEvents
        b <- outEvents
        if !checkABConnected(a, b).contains(false)
      } yield (a, b)
      x
    }

    findABPairs(getIndependentEvents(inputEventsSuperList), getIndependentEvents(outputEventsSuperList))
  }

  /**
    * 5 step: delete (A,B) from W that are not maximal
    * Check if the input and output events of one place are supersets to the input and output events
    * of another place. Place A with input events {a} and output events {b,e}
    * is a superplace of Place B with input {a} and output {b} => Place B can be discarded
    *
    * @param connectedEvents list of a and b that are connected
    * @return list of max (A,B)
    */
  private def findMaximal(connectedEvents: List[(List[Char], List[Char])]): List[(List[Char], List[Char])] = {
    val YL = new ListBuffer() ++ connectedEvents
    for {
      i ← connectedEvents.indices
      a = connectedEvents(i)
      j ← 1 + i until connectedEvents.size
      b = connectedEvents(j)
      if a._1.forall(b._1.contains) && a._2.forall(b._2.contains)
    } {
      YL -= a
    }
    YL.result()
  }

  /**
    * 6 step: set of places
    * place named p (A,B) such that A is the set of input transitions (•p (A,B) = A)
    * and B is the set of output transitions (p (A,B) • = B) of p (A,B) .
    *
    * @return list of event to place transitions
    */
  def makeYL(): List[Place] = {
    val YL = findMaximal(makeXL(causality))
    val TI = initialEvents(eventLog)
    val inPlace = Place(List[Char](), TI)
    val TO = endEvents(eventLog)
    val outPlace = Place(TO, List[Char]())
    var places = new ListBuffer[Place]()
    places += inPlace
    for (p <- YL) {
      places += Place(p._1, p._2)
    }
    places += outPlace
    places.toList
  }

  /*  7 step: set of arcs
   connect source and sink places to the transitions*/

  private def mapPlace(p: Place) = {
    if (p.inEvent.nonEmpty || p.outEvent.nonEmpty) {
      val in = for {
        n <- p.inEvent
      } yield (n, p)
      val out = for {
        n <- p.outEvent
      } yield (p, n)
      in ++ out
    }
  }

  val FL = makeYL().map(x => mapPlace(x))
}
