package ua.com.entity

import scala.util.Random

sealed trait Nucleotide {
  val nucleo: String
}

object Nucleotide {

  case object Adenine extends Nucleotide {
    override val nucleo: String = "A"
  }

  case object Thymine extends Nucleotide {
    override val nucleo: String = "T"
  }

  case object Cytosine extends Nucleotide {
    override val nucleo: String = "C"
  }

  case object Guanine  extends Nucleotide {
    override val nucleo: String = "G"
  }

  def getRandomNucleo: Nucleotide = {
    Random.nextInt(4) match {
    case 0 => Adenine
    case 1 => Thymine
    case 2 => Cytosine
    case 3 => Guanine
    }
  }
}


