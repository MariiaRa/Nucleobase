package software.sigma.alpha

object Main extends App {

  //get complementary base pairs
  private def createLog(input: String): List[String] = {
    println(input.grouped(2).toList)
    input.grouped(2).toList
  }

  val firstlog = createLog("GCATTACGATATATCGCGTACGATATCGCGATTAATATATTAATATCGCGGCGCGCGCATTATAATTAGCCGGCTACGATTATAGCTACGATATATGCTA")
  val validator = new AlphaValidator
  val correctModel = validator.buildCorrectModel(firstlog)
  println(correctModel)


}

