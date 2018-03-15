package software.sigma.nucleoalpha

import org.slf4j.LoggerFactory

class AlphaNucleoValidator {
  private val logger = LoggerFactory.getLogger(this.getClass)

  /**
    * build correct model for futher validation
    *
    * @param eventLog event log over some set of activities, which we feed to alpha algorithm
    * @return alpha algorithm model
    */
  def buildCorrectModel(eventLog: List[String]): List[Place] = {
    val alp = new AlphaForBasePair(eventLog)
    alp.makeYL()
  }

  /**
    * validate input event logs using built correct model
    *
    * @param eventLog event log over some set of activities, which we feed to alpha algorithm
    * @param correctModel alpha algorithm model built using event logs with correct sequences
    * @return correct or incorrect
    */

  def validate(eventLog: List[String], correctModel: List[Place]): Boolean = {
    val alp = new AlphaForBasePair(eventLog)
    val modelForCheck = alp.makeYL()
    if (correctModel.diff(modelForCheck).isEmpty) true
    else false
  }

  /**
    *
    * @param logs list of logs from topic in activeMQ
    * @param correctModel
    * @return
    */
  def calculateRate(logs: List[List[String]], correctModel: List[Place]): BigDecimal = {
    var numberOfCorrectLog: BigDecimal = 0
    var numberOfInCorrectLog: BigDecimal = 0

    for {l <- logs} {
      if (validate(l, correctModel)) numberOfCorrectLog += 1
      else numberOfInCorrectLog += 1
    }
    logger.info(s"Number of correct DNA string: $numberOfCorrectLog")
    logger.info(s"Number of mutated DNA string: $numberOfInCorrectLog")
    val rate: BigDecimal = numberOfInCorrectLog / numberOfCorrectLog
    rate
  }
}