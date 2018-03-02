package software.sigma.alpha

object Main extends App {

  val footprint = new FootprintMartix(List('a', 'b', 'c', 'd', 'e', 'f'), List("abef", "abecdbf", "abcedbf", "abcdebf", "aebcdbf"))

  val directFollowers = footprint.getDirectFollowers
  println(directFollowers)
  val causality = footprint.getCausalities(directFollowers)
  println(causality)
  val parallels = footprint.getParallelism(directFollowers)
  println(parallels)
  val choices = footprint.getExclusiveness(directFollowers)
  println(choices)
  footprint.buildRelations(causality,parallels,choices, directFollowers)

  val alpha = new AlphaAlgorithm(List("abef", "abecdbf", "abcedbf", "abcdebf", "aebcdbf"))

}
