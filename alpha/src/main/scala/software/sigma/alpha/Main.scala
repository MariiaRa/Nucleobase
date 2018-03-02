package software.sigma.alpha

object Main extends App {

  val footprint = new FootprintMartix(List('a', 'b', 'c', 'd', 'e'), List("abcd", "acbd", "aed"))

  val directFollowers = footprint.getDirectFollowers
  println(directFollowers)
  val causality = footprint.getCausalities(directFollowers)
  println(causality)
  val parallels = footprint.getParallelism(directFollowers)
  println(parallels)
  val choices = footprint.getExclusiveness(directFollowers)
  println(choices)
  footprint.buildRelations(causality,parallels,choices)

  val alpha = new AlphaAlgorithm(List("abcd", "acbd", "aed"))

}
