import org.scalatest.{FlatSpec, Inspectors, Matchers}
import software.sigma.alpha.{AlphaAlgorithm, Place}

class TestAlpha extends FlatSpec with Matchers with Inspectors {

  val eventLog1: List[String] = List("abcd", "acdb", "aed")

  val eventLog2: List[String] = List("ABDEFH", "ACGH", "ABEDFH")

  // generate models with the help of alpha algorithm
  val alp1 = new AlphaAlgorithm(eventLog1)
  val alp2 = new AlphaAlgorithm(eventLog2)

  alp1.initialEvents(eventLog1) should have length 1
  alp1.endEvents(eventLog1) should have length 2
  alp1.makeYL() should contain(Place(List('a', 'd'), List('b')))
  alp1.makeYL() should not contain Place(List('a'), List('d'))

  alp1.makeYL() == List(Place(List(), List('a')),
    Place(List('a'), List('b', 'e')),
    Place(List('a'), List('c', 'e')),
    Place(List('b'), List('c')),
    Place(List('a', 'd'), List('b')),
    Place(List('c', 'e'), List('d')),
    Place(List('b', 'd'), List()))

  alp2.initialEvents(eventLog2) should have length 1
  alp2.endEvents(eventLog2) should have length 1
  alp2.makeYL() should contain (Place(List('F', 'G'), List('H')))
  alp2.makeYL() should not contain Place(List('A'), List('H'))

  val places: List[Place] = alp2.makeYL()
  forAll(places) { p =>
    p shouldBe a[Place]
  }
}