package software.sigma.alpha

import scala.collection.mutable.ListBuffer

class AlphaAlgorithm(eventLog: List[String]) {

  private val footprint = new FootprintMartix(eventLog)
  private val directFollowers = footprint.getDirectFollowers
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
    println(ti.distinct)
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
    println(to.distinct)
    to.distinct.sortWith(_ < _)
  }

  /**
    * 4 step: generate list of (A,B) where a in A and b in B are in causality relation, all events in A have independent relations and same for B
    *
    * @param causality a list of event pairs which have relation type of causality
    * @return list of a and b which have relation type of causality
    **/
  def makeXL(causality: List[(Char, Char)]): List[(List[Char], List[Char])] = {

    val inputEvents = causality.map(a => a._1).distinct
    val outputEvents = causality.map(a => a._2).distinct

    val inputEventsSuperList = (1 until inputEvents.size).flatMap(inputEvents.toList.combinations).map(_.toList).toList
    println(inputEventsSuperList)

    val outputEventsSuperList = (1 until outputEvents.size).flatMap(outputEvents.toList.combinations).map(_.toList).toList
    println(outputEventsSuperList)

    def getRelationType(firstEvent: Char, secondEvent: Char): String = {
      val rowIndex: Int = footprint.matrixEventToIndex(firstEvent)
      val colIndex: Int = footprint.matrixEventToIndex(secondEvent)
      footprint.matrix(rowIndex)(colIndex)
    }

    def checkIfConnected(firstEvent: List[Char]) = {
      val t = for {
        i ← 0 until firstEvent.size
        n1 = firstEvent(i)
        j ← 0 until firstEvent.size
        n2 = firstEvent(j)
      } yield getRelationType(n1, n2) != "#"
      t.distinct
    }

    /**
      * firstEvent - a
      * check if all a events in A have independent relations
      *
      * @param inEvent list of all possible combinations of a in A
      * @return list of a events that are independent
      */
    def areAConnected(inEvent: List[List[Char]]) = {
      val t = for {
        a1 <- inEvent
        if (!checkIfConnected(a1).contains(true))
      } yield a1
      t.distinct
    }

    /**
      * secondEvent - b
      * check if all b activities in B have independent relations
      *
      * @param outEvent list of all possible combinations of b in B
      * @return list of b events that are independent
      */
    def areBConnected(outEvent: List[List[Char]]) = {
      val t = for {
        a1 <- outEvent
        if (!checkIfConnected(a1).contains(true))
      } yield a1
      t.distinct
    }

    def checkIfABConnected(inEvent: List[Char], outEvent: List[Char]) = {
      val t = for {
        a <- inEvent
        b <- outEvent
      } yield getRelationType(a, b) == "->"
      t.distinct
    }

    /**
      * For every a in A and b in B => a > b
      *
      * @param inEvents  list of a events that are independent
      * @param outEvents list of b events that are independent
      * @return list of a and b which have relation type of causality
      */
    def findABPairs(inEvents: List[List[Char]], outEvents: List[List[Char]]) = {
      val x = for {
        a <- inEvents
        b <- outEvents
        if (!checkIfABConnected(a, b).contains(false))
      } yield (a, b)
      x
    }

    findABPairs(areAConnected(inputEventsSuperList), areBConnected(outputEventsSuperList))
  }

  /**
    * 5 step: delete (A,B) from W that are not maximal
    * Check if the input and output events of one place are supersets to the input and output events
    * of another place. Place A with input events {a} and output events {b,e}
    * is a superplace of Place B with input {a} and output {b} => Place B can
    * be discarded
    *
    * @param pairs list of a and b pairs
    * @return list of max pairs
    */
  private def findMaximal(pairs: List[(List[Char], List[Char])]) = {
    val YL = new ListBuffer() ++ pairs
    for {
      i ← 0 until pairs.size
      a = pairs(i)
      j ← 1 + i until pairs.size
      b = pairs(j)
      if (a._1.forall(b._1.contains) && a._2.forall(b._2.contains))
    } {
      YL -= a
    }
    YL.result()
  }

  /**
    * 6 step: set of places
    *
    * @return list of event to place transitions
    */
  def makeYL() = {
    val YL = findMaximal(makeXL(causality))
    val TI = initialEvents(eventLog)
    println(TI)
    val inPlace = Place(List[Char](), TI)
    val TO = endEvents(eventLog)
    println(TO)
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
    if (!p.inEvent.isEmpty || !p.outEvent.isEmpty) {
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
