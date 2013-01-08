package nl.jqno.akka_eratosthenes

import org.scalatest.BeforeAndAfterAll
import org.scalatest.Suite

import akka.testkit.TestKit

trait StopSystemAfterAll extends BeforeAndAfterAll {
  this: TestKit with Suite =>
  
  override protected def afterAll() {
    super.afterAll()
    system.shutdown()
  }
}