package nl.jqno.akka_eratosthenes

import scala.collection.mutable.HashMap

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

import PrimeFinder.Master
import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.testkit.TestKit

@RunWith(classOf[JUnitRunner])
class MasterActorTest extends TestKit(ActorSystem("testsystem"))
  with FlatSpec
  with ShouldMatchers
  with StopSystemAfterAll {
  
  behavior of "the MasterActor"
  
  it should "yeah" in {
    val upper = 2
    val actors = HashMap("worker" -> testActor)
    val actor = TestActorRef(new Master(upper, actors))
    
    actor ! Find
    
    expectMsg(Filter(2))
  }
}