package nl.jqno.akka_eratosthenes

object Runner extends App {
  val upper = 30
  val primes = PrimeFinder(upper)
  println(primes)
}