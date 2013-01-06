package nl.jqno.akka_eratosthenes

sealed trait Message
case object Find extends Message
case class Filter(divisor: Int) extends Message
case class Enqueue(list: List[Int]) extends Message
case class Merge(left: List[Int], right: List[Int]) extends Message
case class Result(list: List[Int]) extends Message
