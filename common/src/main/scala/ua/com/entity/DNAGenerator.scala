package ua.com.entity

import ua.com.entity.Nucleotide._

class DNAGenerator {
  private def nextChar(first: Nucleotide): Nucleotide = {
    if (first == Adenine) Thymine
    else if (first == Thymine) Adenine
    else if (first == Guanine) Cytosine
    else Guanine
  }

  def nucleo(a: Nucleotide, bool: Boolean): Stream[Nucleotide] = {
    if (!bool) a #:: nucleo(nextChar(a), !bool)
    else a #:: nucleo(getRandomNucleo, !bool)
  }

  def mutation(a: Nucleotide): Stream[Nucleotide] = {
    a #:: mutation(getRandomNucleo)
  }
}
