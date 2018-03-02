package software.sigma.alpha

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object Footprint extends App {

  val eventLog = List("abcd", "acbd", "aed")
  //val eventLog = List("abd", "acd")


  val traces = for {
    trace <- eventLog
  } yield trace.toList

  println("Traces: " + traces)

  val allEvents = for {
    trace <- traces
    event <- trace
  } yield event

  val seenEvents = allEvents.distinct

  println("Seen events: " + seenEvents)

  // var matrixeventToIndex = collection.mutable.Map[String, String]()
  val footprint = Array.ofDim[String](seenEvents.length, seenEvents.length)
  val matrixeventToIndex = seenEvents.zipWithIndex.toMap

  println(matrixeventToIndex)


  for (x <- 0 until seenEvents.length; y <- 0 until seenEvents.length) {
    footprint(x)(y) = "#"
  }

  /* for (i <- 0 until footprint.length) {
     var line: String = ""
     for (j <- 0 until footprint(0).length) {
       line += footprint(i)(j)+" "
     }
     println(line)
   }*/

  val directFollowers: List[(Char, Char)] = List(('a', 'b'), ('b', 'c'), ('c', 'd'), ('a', 'c'), ('c', 'b'), ('b', 'd'), ('a', 'e'), ('e', 'd'))
  val causality: List[(Char, Char)] = List(('a', 'b'), ('a', 'c'), ('a', 'e'), ('b', 'd'), ('c', 'd'), ('e', 'd'))
  val parallels: List[(Char, Char)] = List(('b', 'c'), ('c', 'b'))
  val choices: List[(Char, Char)] = List(('a', 'd'), ('b', 'e'), ('c', 'e'))

  for {pair1 <- causality
       pair2 <- parallels
       pair3 <- choices
       pair4 <- directFollowers
  } {
    // footprint(matrixeventToIndex(pair4._1))(matrixeventToIndex(pair4._2)) = ">"
     //  footprint(matrixeventToIndex(pair4._2))(matrixeventToIndex(pair4._1)) = "<"

    footprint(matrixeventToIndex(pair1._1))(matrixeventToIndex(pair1._2)) = "->"
    footprint(matrixeventToIndex(pair1._2))(matrixeventToIndex(pair1._1)) = "<-"
    // footprint(matrixeventToIndex(pair1._1))(matrixeventToIndex(pair1._2)) = " <- "
    //  footprint(matrixeventToIndex(pair1._2))(matrixeventToIndex(pair1._1)) = " <- "
    footprint(matrixeventToIndex(pair2._1))(matrixeventToIndex(pair2._2)) = "||"
    footprint(matrixeventToIndex(pair2._2))(matrixeventToIndex(pair2._1)) = "||"
    footprint(matrixeventToIndex(pair3._1))(matrixeventToIndex(pair3._2)) = "#"
  }

  println("\nFootprint matrix:")
  for (i <- 0 until footprint.length) {
    var line: String = ""
    for (j <- 0 until footprint(0).length) {
      line += footprint(i)(j) + " "
    }
    println(line)
  }

  def getRelationType(firstEvent: Char, secondEvent: Char): String = {
    val rowIndex: Int = matrixeventToIndex(firstEvent)
    val colIndex: Int = matrixeventToIndex(secondEvent)
    footprint(rowIndex)(colIndex)
  }

  /*firstEvent   - a
  secondEvent - b
  returns false if a # b, otherwise true*/
  def areConnected(firstEvent: Char, secondEvent: Char) = getRelationType(firstEvent, secondEvent) != "#"

  /*firstEvent   - a
   secondEvent - b
   returns true if a > b, otherwise false*/
  def isDirectlyFollowed(firstEvent: Char, secondEvent: Char): Boolean = getRelationType(firstEvent, secondEvent) == ">"

val inputEvents = (for{
  a <- directFollowers ::: causality
  } yield a._1).distinct
  println("\nInput events "+inputEvents)

  val outputEvents = (for{
    a <- directFollowers ::: causality
  } yield a._2).distinct
  println("\nOutput events "+outputEvents)
  /*
   * (A,B), A = first, B = second
*/
    // For every a1,a2 in A => a1#a2

  // For every b1, b2 in B => b1#b2


  // For every a in A and b in B => a > b in f



val testSubSets1 = (1 until  inputEvents.size).flatMap(inputEvents.toList.combinations).map(_.toList).toList
  println(testSubSets1)
val testSubSets2 = (1 until outputEvents.size).flatMap(outputEvents.toList.combinations).map(_.toList).toList
  println(testSubSets2)


 val seenEventsSuperList = (1 to  seenEvents.size).flatMap(seenEvents.toList.combinations).map(_.toList).toList
  println("SuperList: "+seenEventsSuperList)

  println("SuperList1: "+seenEvents.toSet.subsets.map(_.toList).toList)

  val options = new mutable.MutableList()
  val builder = List.newBuilder[(List[Char], List[Char])]


  for (i <- 0 until seenEventsSuperList.length) {
    val first: List[Char]= seenEventsSuperList(i)
  //println("first"+first )
    for (j <- 0 until first.length) {
      if (i != j) {
         val second: List[Char] = seenEventsSuperList(j)
      //  println("second"+second )
        if (first != second)
       //   println  (second, first)
       builder += ((second, first))
      }
    }
  }


  val TI = List('a')

  val inPlace = Place(List[Char](),TI)

  val TO = List('d')
  val outPlace = Place(TO , List[Char]())

  println()

 // println(directFollowers)
 // println(parallels)
 // println("Casualities"+causality)
  val superCas = causality.map{ case (a,b) => List(a,b) }
  println("flatCasual: "+superCas)


//step 4-5
 val superPrl = parallels.map{ case (a,b) => List(a,b) }
  println("flat: "+parallels.map{ case (a,b) => List(a,b) })
  val superChoice = choices.map{ case (a,b) => List(a,b) }
  println("flatCh: "+superChoice)
  val transitions = for {
    set <- seenEventsSuperList
    prl <- superPrl
    cas <- superCas
    if !superPrl.contains(set)
    if !superChoice.contains(set)
    if set.size > 1
    if !prl.forall(set.contains)
    if cas.forall(set.contains)
    if !(TI:::TO).forall(set.contains)
       } yield set

  println(transitions.distinct)

def reducetransitions(allTransitions: List[List[Char]]): List[List[Char]] = {
 val reducedPlaces = for{
    p1 <- transitions
    p2 <-transitions
    if (p1 != p2)
    if !p1.forall(p2.contains)
    if p1.length<p2.length
  } yield  p2
  reducedPlaces.distinct
}

  val YL = reducetransitions(transitions.distinct)

 println(YL)

val first = (for {
    p <- YL
    if TI.forall(p.contains)
    } yield (TI, p.diff(TI))).distinct

  val second = (for {
    p <- YL
    if TO.forall(p.contains)
  } yield (p.diff(TO), TO)).distinct

  val YLformatted = first ++ second
println("YLformatted"+YLformatted)

//step 6
  var places = new ListBuffer[Place]()
  places += inPlace

  for(p <- YLformatted) {places += Place(p._1, p._2)}
places +=outPlace

  val placeList = places.toList

  println(placeList)

  //step7

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

  val FL = placeList.map( x => mapPlace(x))
println(FL)
}


