package software.sigma.alpha

object Alpha extends App {

  val list2 = List("abcd", "acbd", "aed")


  def createLog(input: String): List[String] = {
    println(input.grouped(2).toList)
    input.grouped(2).toList
  }

  def allEvents(list: List[String]) = {
    val tl = for (
      pair <- list;
      n <- pair) yield n
    tl.distinct
  }

  def initialEvents(list: List[String]) = {
    // val ti = scala.collection.mutable.Set[Char]()
    val ti = for (pair <- list) yield pair(0)
    ti.distinct
  }

  def endEvents(list: List[String]): List[Char] = {
    val to = for (pair <- list) yield pair(pair.length - 1)
    to.distinct
  }

  def allTransitions(list: List[String]): List[String] = {
    list.distinct
  }

  def directFollower(list: List[String]): List[(Char, Char)] = {
    val followers = for {
      pair <- list
      (event, index) <- pair.zipWithIndex
      if index != pair.length - 1
    } yield (event, pair(index + 1))
    followers.distinct
  }

  def causality(list: List[String], allEvents: List[Char], df: List[(Char, Char)]): List[(Char, Char)] = {
    val casualities = for {
      n1 <- allEvents
      n2 <- allEvents
      if df.contains((n1, n2)) && !df.contains((n2, n1))
    } yield (n1, n2)
    casualities.distinct
  }

  def parallelism(list: List[String], allEvents: List[Char], df: List[(Char, Char)]): List[(Char, Char)] = {
    val parallels = for {
      n1 <- allEvents
      n2 <- allEvents
      if df.contains((n1, n2)) && df.contains((n2, n1))
    } yield (n1, n2)
    parallels.distinct
  }

   def choices(allEvents: List[Char], df: List[(Char, Char)]) = {
    val test = for {
      i ← allEvents.indices
      n1 = allEvents(i)
      j ← i + 1 until allEvents.size
      n2 = allEvents(j)
      if !df.contains((n1, n2)) && !df.contains((n2, n1)) && n1 != n2
    } yield (n1, n2)
    test.toList
  }



   //val list = createLog("ATATTACGCGTAATTAGCCGTATAGCGCCGATCGTATATAATTACGATATATGCTATATAGCGCCGGCATCGGCATTAATCGTATATATATAATCGCGCG")

  // val list2 = List("ABD", "ACD")

  //val list2= List("abcd", "acbd", "aed")

  println("TI set: " + initialEvents(list2))
  println("TO set: " + endEvents(list2))
  println("All activities: " + allEvents(list2))
  println(allTransitions(list2))
  println("Direct followers: " + directFollower(list2))
  println("Casualities: " + causality(list2, allEvents(list2), directFollower(list2)))
  println("Parallels: " + parallelism(list2, allEvents(list2), directFollower(list2)))
  println("Choices: " + choices(allEvents(list2), directFollower(list2)))

}

