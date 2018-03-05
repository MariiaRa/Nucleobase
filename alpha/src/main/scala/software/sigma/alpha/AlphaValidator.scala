package software.sigma.alpha

class AlphaValidator {

  def buildCorrectModel(eventLog: List[String]): List[Place] = {
    val alp = new AlphaForBasePair(eventLog)
    alp.makeYL()
  }

  def validate(eventLog: List[String], correctModel: List[Place]): Boolean = {
    val alp = new AlphaForBasePair(eventLog)
    val modelForCheck = alp.makeYL()
    if (correctModel.diff(modelForCheck).isEmpty) true
    else false
  }

  def calculateRate(logs: List[List[String]], correctModel: List[Place]): Double = {
    var numberOfCorrectLog: Double = 0
    var numberOfInCorrectLog: Double = 0
    val MAX_WAIT = 1000
    val millis = System.currentTimeMillis()
    while (System.currentTimeMillis() - millis <= MAX_WAIT) {
      for {l <- logs} {
        if (validate(l, correctModel)) numberOfCorrectLog += 1
        else numberOfInCorrectLog += 1
      }
    }
  // println(numberOfCorrectLog)
   // println(numberOfInCorrectLog)
    numberOfInCorrectLog / numberOfCorrectLog
  }
}