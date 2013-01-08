package nl.jqno.akka_eratosthenes

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class PrimeFinderTest extends FlatSpec with ShouldMatchers {
  
  behavior of "the PrimeFinder"
  
  it should "return a list of primes with an upper bound" in {
    PrimeFinder(10) should be (List(2, 3, 5, 7))
  }
  
  it should "return a singleton list if the upper bound is 2" in {
    PrimeFinder(2) should be (List(2))
  }
  
  it should "return an empty list when there are no primes below the upper bound" in {
    PrimeFinder(1) should be (Nil)
    PrimeFinder(0) should be (Nil)
    PrimeFinder(-10) should be (Nil)
  }
  
  it should "find the 100th prime" in {
    PrimeFinder(545).last should be (541)
  }
}