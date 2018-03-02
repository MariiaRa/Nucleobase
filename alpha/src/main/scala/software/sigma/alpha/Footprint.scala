package software.sigma.alpha

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
  //val footprint = Array.ofDim[String](seenEvents.length, seenEvents.length)
 // val matrixeventToIndex = seenEvents.zipWithIndex.toMap

 // println(matrixeventToIndex)


 /* for (x <- 0 until seenEvents.length; y <- 0 until seenEvents.length) {
    footprint(x)(y) = "#"
  }*/

  val footprint = new FootprintMartix(List('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'), List("ABDEFH", "ACGH", "ABEDFH"))

  //val footprint = new FootprintMartix(List('a', 'b', 'c', 'd', 'e'), List("abcd", "acbd", "aed"))

  //val footprint = new FootprintMartix(List('a', 'b', 'c', 'd'), List("abd", "acd"))
 // val footprint = new FootprintMartix(List('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'), List("ABCEFGH", "ABCEGFH", "ADEGFH", "ADEFGH"))

  //val footprint = new FootprintMartix(List('a', 'b', 'c', 'd', 'e', 'f'), List("abef", "abecdbf", "abcedbf","abcdebf", "aebcdbf" ))

  val directFollowers = footprint.getDirectFollowers
  println("D "+directFollowers)
  val causality = footprint.getCausalities(directFollowers)
  println("C " + causality)
  val parallels = footprint.getParallelism(directFollowers)
  println("P " +parallels)
  val choices = footprint.getExclusiveness(directFollowers)
  println("Ch "+ choices)
  footprint.buildRelations(causality,parallels,choices, directFollowers)
val xxx = (directFollowers ::: causality).diff(parallels)
  println((directFollowers ::: causality).diff(parallels))


  val inputEvents = (for{
    a <- xxx
  } yield a._1).distinct
  println("\nInput events "+inputEvents)

  val outputEvents = (for{
    a <- xxx
  } yield a._2).distinct
println("\nOutput events "+outputEvents)
  /*
   * (A,B), A = first, B = second
*/
  // For every a1,a2 in A => a1#a2
  val inputEventsSuperList = (1 until  inputEvents.size).flatMap(inputEvents.toList.combinations).map(_.toList).toList
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
/*  println(checkIfConnected(List('b', 'c', 'e')))
  println(checkIfConnected(List('b','e')))*/


  /*firstEvent   - a
check if all  a activities in A have independent relations*/
  def areAConnected(inEvent: List[List[Char]]) = {
val t = for {
  a1 <- inEvent
 // a2 <- outEvent
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

  println(areAConnected(inputEventsSuperList))
  println(areBConnected(outputEventsSuperList))


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
    x
  }

println(findABPairs(areAConnected(inputEventsSuperList), areBConnected(outputEventsSuperList)))


 /* /*firstEvent   - a
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
    val testSubSets1 = (1 until  inputEvents.size).flatMap(inputEvents.toList.combinations).map(_.toList).toList
  println(testSubSets1)




  // For every b1, b2 in B => b1#b2


  // For every a in A and b in B => a > b in f










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
println(FL)*/
}


