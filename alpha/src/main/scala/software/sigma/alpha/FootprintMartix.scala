package software.sigma.alpha

class FootprintMartix(eventLog: List[String]) {

  val seenEvents = (for {
    trace <- eventLog
    event <- trace
  } yield event).distinct

  val matrix = Array.ofDim[String](seenEvents.length, seenEvents.length)
  //fill the matrix of relations
  for (x <- 0 until seenEvents.length; y <- 0 until seenEvents.length) {
    matrix(x)(y) = "#"
  }

  val matrixEventToIndex: Map[Char, Int] = seenEvents.zipWithIndex.toMap

  //Find all relations
  //Direct follower : a > bw are in execution seqeunce iff b directly follows a
  def getDirectFollowers() = {
    val followers = for {
      pair <- eventLog
      (event, index) <- pair.zipWithIndex
      if index != pair.length - 1
    } yield (event, pair(index + 1))
    followers.distinct
  }

  //Causality : a → bw iff a > w b but not b > w a
  def getCausalities(df: List[(Char, Char)]): List[(Char, Char)] = {
    val casualities = for {
      n1 <- seenEvents
      n2 <- seenEvents
      if df.contains((n1, n2)) && !df.contains((n2, n1))
    } yield (n1, n2)
    casualities.distinct
  }

  //Parallelism : a ║ w b iff a > bw and b > aw
  def getParallelism(df: List[(Char, Char)]): List[(Char, Char)] = {
    val parallels = for {
      n1 <- seenEvents
      n2 <- seenEvents
      if df.contains((n1, n2)) && df.contains((n2, n1))
    } yield (n1, n2)
    parallels.distinct
  }

  //Exclusiveness : a # bw iff not a > bw and not b > aw
  def getExclusiveness(df: List[(Char, Char)]) = {
    val test = for {
      i ← seenEvents.indices
      n1 = seenEvents(i)
      j ← i + 1 until seenEvents.size
      n2 = seenEvents(j)
      if !df.contains((n1, n2)) && !df.contains((n2, n1)) && n1 != n2
    } yield (n1, n2)
    test.distinct.toList
  }

  def buildRelations(causality: List[(Char, Char)], parallels: List[(Char, Char)], choices: List[(Char, Char)], directFollowers: List[(Char, Char)]): Unit = {
    if (parallels.nonEmpty) {
      for {
        pair4 <- directFollowers
      } {
        matrix(matrixEventToIndex(pair4._1))(matrixEventToIndex(pair4._2)) = ">"
        matrix(matrixEventToIndex(pair4._2))(matrixEventToIndex(pair4._1)) = "<"
      }
    }

    if (causality.nonEmpty) {
      for {
        pair1 <- causality
      } {
        matrix(matrixEventToIndex(pair1._1))(matrixEventToIndex(pair1._2)) = "->"
        matrix(matrixEventToIndex(pair1._2))(matrixEventToIndex(pair1._1)) = "<-"
      }
    }

    if (parallels.nonEmpty) {
      for {
        pair2 <- parallels
      } {
        matrix(matrixEventToIndex(pair2._2))(matrixEventToIndex(pair2._1)) = "||"
        matrix(matrixEventToIndex(pair2._1))(matrixEventToIndex(pair2._2)) = "||"
      }
    }

 /*   println("Footprint matrix:")
    for (i <- 0 until matrix.length) {
      var line: String = ""
      for (j <- 0 until matrix(0).length) {
        line += matrix(i)(j) + " "
      }
      println(line)
    }*/
  }
}
