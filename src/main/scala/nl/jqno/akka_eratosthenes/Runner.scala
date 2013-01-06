package nl.jqno.akka_eratosthenes

object Runner extends App {
  val upper = 30
  val finder = new PrimeFinder(upper)
  val primes = finder.start
  println(primes)
}