package software.sigma.alpha

class AlphaAlgorithm(log: List[String]) {

  //get complementary base pairs
  /* private def createLog(input: String): List[String] = {
     println(input.grouped(2).toList)
     input.grouped(2).toList
   }*/

  //1 step: get Tw - set of distinct activities in W (workflow log)
  //gather all seen events from log
  private def getAllSeenEvents(log: List[String]): List[Char] = {
    val seenEvents = for (
      trace <- log;
      event <- trace) yield event
    seenEvents.distinct
  }

  //2 step: get Ti - set of start activities, first element in each trace in W
  private def initialEvents(log: List[String]): List[Char] = {
    val ti = for (pair <- log) yield pair(0)
    ti.distinct
  }

  //3 step: get To - set of end activities, last element in each trace in W
  private def endEvents(log: List[String]): List[Char] = {
    val to = for (pair <- log) yield pair(pair.length - 1)
    to.distinct
  }

  //val footprint = new FootprintMartix(List('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'), List("ABDEFH", "ACGH", "ABEDFH"))
  val footprint = new FootprintMartix(List('A', 'B', 'C', 'D', 'E'), List("ABCEBCD", "ABCD"))
  //val footprint = new FootprintMartix(List('a', 'b', 'c', 'd', 'e'), List("abcd", "acbd", "aed"))
  //val footprint = new FootprintMartix(List('a', 'b', 'c', 'd'), List("abd", "acd"))
  // val footprint = new FootprintMartix(List('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'), List("ABCEFGH", "ABCEGFH", "ADEGFH", "ADEFGH"))
  //val footprint = new FootprintMartix(List('a', 'b', 'c', 'd', 'e', 'f'), List("abef", "abecdbf", "abcedbf","abcdebf", "aebcdbf" ))

  val directFollowers = footprint.getDirectFollowers
 // println("D " + directFollowers)
  val causality = footprint.getCausalities(directFollowers)
 // println("C " + causality)
  val parallels = footprint.getParallelism(directFollowers)
 // println("P " + parallels)
  val choices = footprint.getExclusiveness(directFollowers)
 // println("Ch " + choices)
  footprint.buildRelations(causality, parallels, choices, directFollowers)
  val allTransitions = (directFollowers ::: causality).diff(parallels)
  //println((directFollowers ::: causality).diff(parallels))

  val inputEvents = (for {a <- allTransitions} yield a._1).distinct
 // println("\nInput events " + inputEvents)

  val outputEvents = (for {a <- allTransitions} yield a._2).distinct
 // println("\nOutput events " + outputEvents)

  //4 step: set of (A,B) where a in A and b in B are in causality relation, all activities in A independent relation and same for B
  //5 step: delete (A,B) from X W that are not maximal
  /*
   * (A,B), A = first, B = second
*/

  val inputEventsSuperList = (1 until inputEvents.size).flatMap(inputEvents.toList.combinations).map(_.toList).toList
  val outputEventsSuperList = (1 until outputEvents.size).flatMap(outputEvents.toList.combinations).map(_.toList).toList


  def getRelationType(firstEvent: Char, secondEvent: Char): String = {
    val rowIndex: Int = footprint.matrixEventToIndex(firstEvent)
    val colIndex: Int = footprint.matrixEventToIndex(secondEvent)
    footprint.matrix(rowIndex)(colIndex)
  }

  //check relations
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
check if all a activities in A have independent relations*/
  def areAConnected(inEvent: List[List[Char]]) = {
    val t = for {
      a1 <- inEvent
      // a2 <- outEvent
      if (!checkIfConnected(a1).contains(true))
    } yield a1
    t.distinct
  }

  /*secondEvent - b check if all b activities in B have independent relations*/
  def areBConnected(outEvent: List[List[Char]]) = {
    val t = for {
      a1 <- outEvent
      if (!checkIfConnected(a1).contains(true))
    } yield a1
    t.distinct
  }

  // println(areAConnected(inputEventsSuperList))
  // println(areBConnected(outputEventsSuperList))

  // For every a in A and b in B => a > b in relations
  def checkIfABConnected(inEvent: List[Char], outEvent: List[Char]) = {
    val t = for {
      a <- inEvent
      b <- outEvent
    } yield getRelationType(a, b) == "->"
    t.distinct
  }

  def findABPairs(inEvents: List[List[Char]], outEvents: List[List[Char]]) = {
    val x = for {
      a <- inEvents
      b <- outEvents
      if (!checkIfABConnected(a, b).contains(false))
    } yield (a, b)
    x.distinct
  }

  //println(findABPairs(areAConnected(inputEventsSuperList), areBConnected(outputEventsSuperList)))

  //step7: set of arcs
 /* def mapPlace(p: Place) = {
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

  val FL = placeList.map(x => mapPlace(x))*/

}
