package ua.com

import ua.com.entity.Nucleotide
import ua.com.entity.Nucleotide._

object Test extends App {
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

 nucleo(getRandomNucleo,false) take 30 foreach (n => print(n.nucleo))
  println("\n")
  mutation(getRandomNucleo) take 40 foreach (n => print(n.nucleo))
}
