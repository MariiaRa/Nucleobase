package ua.com.entity
import ua.com.entity.NucleotideTransition._

class DNAGenerator {

  /**
    * generate correct DNA string
    *
    * @param nucleobase nucleotide (A, T, G, C)
    * @param check for checking if nucleobase in stream is already paired
    * @return stream of nucleobases with correct pairing
    */
  def buildDNA(nucleobase: Nucleotides, check: Boolean): Stream[Nucleotides] = {
    if (!check) nucleobase #:: buildDNA(getCompelement(nucleobase), !check)
    else nucleobase #:: buildDNA(getRandomNucleo, !check)
  }

  /**
    * introduce mutations
    *
    * @param nucleobase nucleotide (A, T, G, C)
    * @return stream of random nucleobases
    */
  def putMutation(nucleobase: Nucleotides): Stream[Nucleotides] = {
    nucleobase #:: putMutation(getRandomNucleo)
  }
}
