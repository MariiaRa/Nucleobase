package ua.com.entity
import ua.com.entity.NucleotideTransition._

class DNAGenerator {

  //generate correct DNA string
  def nucleo(a: Nucleotides, bool: Boolean): Stream[Nucleotides] = {
    if (!bool) a #:: nucleo(transition(a), !bool)
    else a #:: nucleo(getRandomNucleo, !bool)
  }

  //introduce mutations
  def mutation(a: Nucleotides): Stream[Nucleotides] = {
    a #:: mutation(getRandomNucleo)
  }
}
