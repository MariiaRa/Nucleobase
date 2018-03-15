package software.sigma.alpha

class FootprintMartix(eventLog: List[String]) {

  val seenEvents: List[Char] = (for {
    trace <- eventLog
    event <- trace
  } yield event).distinct

  val matrix = Array.ofDim[String](seenEvents.length, seenEvents.length)

  // fill the matrix of relations
  for (x <- seenEvents.indices; y <- seenEvents.indices) {
    matrix(x)(y) = "#"
  }

  val matrixEventToIndex: Map[Char, Int] = seenEvents.zipWithIndex.toMap

  /**
    * Direct follower : a > b are in execution seqeunce iff b directly follows a
    *
    * @param eventLog event log over some set of activities, which we feed to alpha algorithm
    * @return list of direct followers
    */
  def getDirectFollowers(eventLog: List[String]): List[(Char, Char)] = {
    val followers = for {
      pair <- eventLog
      (event, index) <- pair.zipWithIndex
      if index != pair.length - 1
    } yield (event, pair(index + 1))
    followers.distinct
  }

  /**
    * Causality : a → b iff a > b but not b > a
    *
    * @param df list of direct followers
    * @return list of causalities
    */
  def getCausalities(df: List[(Char, Char)]): List[(Char, Char)] = {
    val casualities = for {
      n1 <- seenEvents
      n2 <- seenEvents
      if df.contains((n1, n2)) && !df.contains((n2, n1))
    } yield (n1, n2)
    casualities.distinct
  }

  /**
    * Parallelism : a ║ b iff a > b and b > a
    *
    * @param df list of direct followers
    * @return list of parallel events
    */
  def getParallelism(df: List[(Char, Char)]): List[(Char, Char)] = {
    val parallels = for {
      n1 <- seenEvents
      n2 <- seenEvents
      if df.contains((n1, n2)) && df.contains((n2, n1))
    } yield (n1, n2)
    parallels.distinct
  }

  //Exclusiveness : a # b iff not a > b and not b > a
  /**
    *
    * @param df list of direct followers
    * @return list of events that have no relatuons
    */
  def getExclusiveness(df: List[(Char, Char)]): List[(Char, Char)] = {
    val test = for {
      i ← seenEvents.indices
      n1 = seenEvents(i)
      j ← i + 1 until seenEvents.size
      n2 = seenEvents(j)
      if !df.contains((n1, n2)) && !df.contains((n2, n1)) && n1 != n2
    } yield (n1, n2)
    test.distinct.toList
  }

  /**
    * build matrix of all relations
    *
    * @param causalities     events with causality relations
    * @param parallels       events that are parallel
    * @param choices         list of events that have no relatuons
    * @param directFollowers direct followers
    */
  def buildRelations(causalities: List[(Char, Char)], parallels: List[(Char, Char)], choices: List[(Char, Char)], directFollowers: List[(Char, Char)]): Unit = {
    if (parallels.nonEmpty) {
      for {
        df <- directFollowers
      } {
        matrix(matrixEventToIndex(df._1))(matrixEventToIndex(df._2)) = ">"
        matrix(matrixEventToIndex(df._2))(matrixEventToIndex(df._1)) = "<"
      }
    }

    if (causalities.nonEmpty) {
      for {
        causality <- causalities
      } {
        matrix(matrixEventToIndex(causality._1))(matrixEventToIndex(causality._2)) = "->"
        matrix(matrixEventToIndex(causality._2))(matrixEventToIndex(causality._1)) = "<-"
      }
    }

    if (parallels.nonEmpty) {
      for {
        parallel <- parallels
      } {
        matrix(matrixEventToIndex(parallel._2))(matrixEventToIndex(parallel._1)) = "||"
        matrix(matrixEventToIndex(parallel._1))(matrixEventToIndex(parallel._2)) = "||"
      }
    }
  }
}
