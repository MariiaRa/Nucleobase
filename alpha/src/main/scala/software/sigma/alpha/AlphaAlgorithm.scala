package software.sigma.alpha

import scala.collection.mutable.ListBuffer

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

  //4 step: set of (A,B) where a in A and b in B are in causality relation, all activities in A independent relation and same for B
  //5 step: delete (A,B) from X W that are not maximal
  private def getTransitions(): List[(List[Char], List[Char])] = {
    val seenEvents = getAllSeenEvents(log)

    val seenEventsSuperList = (1 to seenEvents.size).flatMap(seenEvents.toList.combinations).map(_.toList).toList
    //println("SuperList: " + seenEventsSuperList)

    //get relations
    val footprint = new FootprintMartix(seenEvents, log)
    val directFollowers = footprint.getDirectFollowers
    val listOfCausalities = footprint.getCausalities(directFollowers).map { case (a, b) => List(a, b) }
    //println("casualities: " + listOfCausalities)
    val listOfParallels = footprint.getParallelism(directFollowers).map { case (a, b) => List(a, b) }
    //println("parallels: " + listOfParallels)
    val listOfChoices = footprint.getExclusiveness(directFollowers).map { case (a, b) => List(a, b) }
    //println("exclude: " + listOfChoices)

    //filter all possible combinations of events against relations
    val transitions = for {
      set <- seenEventsSuperList
      prl <- listOfParallels
      cas <- listOfCausalities
      if !listOfParallels.contains(set)
      if !listOfChoices.contains(set)
      if set.size > 1
      if !prl.forall(set.contains)
      if cas.forall(set.contains)
      if !(initialEvents(log) ::: endEvents(log)).forall(set.contains)
    } yield set
    transitions.distinct
println("XL: " + transitions.distinct)
    val reducedPlaces = for {
      p1 <- transitions.distinct
      p2 <- transitions.distinct
      if (p1 != p2)
      if !p1.forall(p2.contains)
      if p1.length < p2.length
    } yield p2
    reducedPlaces.distinct

    val first = (for {
      p <- reducedPlaces.distinct
      if initialEvents(log).forall(p.contains)
    } yield (initialEvents(log), p.diff(initialEvents(log)))).distinct

    val second = (for {
      p <- reducedPlaces.distinct
      if endEvents(log).forall(p.contains)
    } yield (p.diff(endEvents(log)), endEvents(log))).distinct

    val YLformatted = first ++ second
    println(YLformatted)
    YLformatted
  }

  //6 step: set of places
  // get the start place i and the final state o
  val allTransitions = getTransitions()
  val inPlace = Place(List[Char](), initialEvents(log))
  val outPlace = Place(endEvents(log), List[Char]())
  var places = new ListBuffer[Place]()
  places += inPlace
  for (p <- allTransitions) {
    places += Place(p._1, p._2)
  }
  places += outPlace
  val placeList = places.toList
  //println(placeList)

  //step7: set of arcs
  def mapPlace(p: Place) = {
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
  val FL = placeList.map(x => mapPlace(x))
  //println(FL)
}
