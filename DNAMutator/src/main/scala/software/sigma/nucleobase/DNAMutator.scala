package software.sigma.nucleobase

import java.util.{Timer, TimerTask}

import org.slf4j.LoggerFactory
import ua.com.Publisher
import ua.com.entity.{DNAGenerator, Nucleotide}
import ua.com.entity.Nucleotide._

object DNAMutator extends App {
  private val logger = LoggerFactory.getLogger(this.getClass)
  val generator = new DNAGenerator
  val nucleoStream: Stream[Nucleotide] = generator.mutation(getRandomNucleo)
  val publisher = new Publisher
  logger.info("DMA mutator has started.")
  val task: TimerTask = new TimerTask() {
    def run() {
      nucleoStream take 10 foreach (n => publisher.send(n.nucleo))
    }
  }
  val timer = new Timer()
  val delay = 0
  val intevalPeriod = 5 * 1000
  // schedules the task to be run in an interval
  timer.scheduleAtFixedRate(task, delay, intevalPeriod)
 // publisher.closeConnection()
}
