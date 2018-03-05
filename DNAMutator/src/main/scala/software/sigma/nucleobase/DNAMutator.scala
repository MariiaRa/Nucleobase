package software.sigma.nucleobase

import ua.com.Publisher
import ua.com.entity.{DNAGenerator, Nucleotide}
import ua.com.entity.Nucleotide._

object DNAMutator extends App {

  val generator = new DNAGenerator
  val NucleoStream: Stream[Nucleotide] = generator.mutation(getRandomNucleo)
  val publisher = new Publisher("tcp://localhost:61616", "DNA", "DNAMutator")

  NucleoStream take 100 foreach (n => publisher.send(n.nucleo))
  publisher.closeConnection()
}
