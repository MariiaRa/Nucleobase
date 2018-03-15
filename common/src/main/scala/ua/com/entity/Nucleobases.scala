package ua.com.entity

import scala.util.Random

sealed trait Nucleotides {
  val nucleo: String
}

case object Adenine extends Nucleotides {
  override val nucleo: String = "A"
}

case object Thymine extends Nucleotides {
  override val nucleo: String = "T"
}

case object Cytosine extends Nucleotides {
  override val nucleo: String = "C"
}

case object Guanine extends Nucleotides {
  override val nucleo: String = "G"
}

object NucleotideTransition {

  /**
    * perform nucleobase complementation
    *
    * @param nucleobase
    * @return
    */
  def getCompelement(nucleobase: Nucleotides): Nucleotides = {
    nucleobase match {
      case Adenine => Thymine
      case Thymine => Adenine
      case Cytosine => Guanine
      case Guanine => Cytosine
    }
  }

  def getRandomNucleo: Nucleotides = {
    Random.nextInt(4) match {
      case 0 => Adenine
      case 1 => Thymine
      case 2 => Cytosine
      case 3 => Guanine
    }
  }
}