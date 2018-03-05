package software.sigma.alpha

import scala.collection.mutable.ListBuffer

class AlphaForBasePair(eventLog: List[String]) {
  private val footprint = new FootprintMartix(eventLog)
  private val directFollowers = footprint.getDirectFollowers
  private val causality = directFollowers
  private val parallels = List.empty
  private val choices = List.empty
  footprint.buildRelations(causality, parallels, choices, directFollowers)
  //1 step: get Tw - set of distinct activities in W (workflow log)
  //gather all seen events from log
  private def getAllSeenEvents(eventLog: List[String]): List[Char] = {
    val seenEvents = for (
      trace <- eventLog;
      event <- trace) yield event
    seenEvents.distinct
  }

  //2 step: get Ti - set of start activities, first element in each trace in W
  def initialEvents(eventLog: List[String]): List[Char] = {
    val ti = for (pair <- eventLog) yield pair(0)
    println("TI "+ti.distinct)
    ti.distinct
  }

  //3 step: get To - set of end activities, last element in each trace in W
  def endEvents(eventLog: List[String]): List[Char] = {
    val to = for (pair <- eventLog) yield pair(pair.length - 1)
    println("TO "+to.distinct)
    to.distinct
  }

  def makeXL(): List[(List[Char], List[Char])] = {

    val inputEvents = causality.map(a => a._1).distinct
    val outputEvents = causality.map(a => a._2).distinct

    val inputEventsSuperList = (1 until inputEvents.size).flatMap(inputEvents.toList.combinations).map(_.toList).toList
   // println("superset1: "+inputEventsSuperList)

    val outputEventsSuperList = (1 until outputEvents.size).flatMap(outputEvents.toList.combinations).map(_.toList).toList
    //println("superset2: "+outputEventsSuperList)

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

    /*firstEvent   - a
    check if all  a activities in A have independent relations*/
    def areAConnected(inEvent: List[List[Char]]) = {
      val t = for {
        a1 <- inEvent
        if (!checkIfConnected(a1).contains(true))
      } yield a1
      t.distinct
    }

    /*secondEvent   - b
    check if all b activities in B have independent relations*/
    def areBConnected(outEvent: List[List[Char]]) = {
      val t = for {
        a1 <- outEvent
        if (!checkIfConnected(a1).contains(true))
      } yield a1
      t.distinct
    }
  // println("Connected "+areAConnected(inputEventsSuperList))
// println("Connected "+areBConnected(outputEventsSuperList))

    // For every a in A and b in B => a > b in f
    def checkIfABConnected(inEvent: List[Char], outEvent: List[Char]) = {
      val t = for {
        a <- inEvent
        b <- outEvent
        /*relations = getRelationType(a, b)
        if (relations == "->" || relations == "<-")*/
      } yield getRelationType(a, b) != "#"
      t.distinct
    }

    def findABPairs(inEvents: List[List[Char]], outEvents: List[List[Char]]) = {
      val x = for {
        a <- inEvents
        b <- outEvents
        if (!checkIfABConnected(a, b).contains(false))
      } yield (a, b)
      x
    }
    // println(findABPairs(areAConnected(inputEventsSuperList), areBConnected(outputEventsSuperList)))
    findABPairs(areAConnected(inputEventsSuperList), areBConnected(outputEventsSuperList))
  }

  //5 step: delete (A,B) from W that are not maximal
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

  //6 step: set of places
  def makeYL() = {
    val YL = findMaximal(makeXL())
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

  //step7: set of arcs
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

