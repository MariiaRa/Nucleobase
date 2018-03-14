package software.sigma.alpha

import org.slf4j.LoggerFactory

class AlphaValidator {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def buildCorrectModel(eventLog: List[String]): List[Place] = {
    val alp = new AlphaAlgorithm(eventLog)
    alp.makeYL()
  }

  def validate(eventLog: List[String], correctModel: List[Place]): Boolean = {
    val alp = new AlphaAlgorithm(eventLog)
    val modelForCheck = alp.makeYL()
    if (correctModel.diff(modelForCheck).isEmpty) true
    else false
  }

  def calculateRate(logs: List[List[String]], correctModel: List[Place]): Double = {
    var numberOfCorrectLog: Double = 0
    var numberOfInCorrectLog: Double = 0

    for {l <- logs} {
      if (validate(l, correctModel)) numberOfCorrectLog += 1
      else numberOfInCorrectLog += 1
    }
    logger.info(s"Number of correct DNA string: $numberOfCorrectLog")
    logger.info(s"Number of mutated DNA string: $numberOfInCorrectLog")
    val rate: Double = numberOfInCorrectLog / numberOfCorrectLog
    rate
  }
}
