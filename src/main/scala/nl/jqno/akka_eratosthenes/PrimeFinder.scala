package nl.jqno.akka_eratosthenes

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.dispatch.Await
import akka.pattern.ask
import akka.routing.RoundRobinRouter
import akka.util.Timeout
import akka.util.duration.intToDurationInt

class PrimeFinder(upper: Int, nrOfWorkers: Int = 2) {
  val system = ActorSystem("EratosthenesSystem")
  val master = system.actorOf(Props(new Master), name = "master")
  val queuer = system.actorOf(Props(new Queuer), name = "queuer")
  val worker = system.actorOf(Props(new Worker) withRouter RoundRobinRouter(nrOfWorkers), name = "worker")
  
  private class Master extends Actor {
    var source: ActorRef = _
    
    def receive = {
      case Find =>
        source = sender
        (2 to upper) foreach { n => worker ! Filter(n) }
      case Result(list) =>
        source ! list
    }
  }
  
  private class Queuer extends Actor {
    private var other: Option[List[Int]] = None
    private var expectedMessages = ((upper - 1) * 2) - 1
    
    def receive = {
      case Enqueue(list) =>
        expectedMessages -= 1
        if (expectedMessages == 0) {
          master ! Result(list)
        }
        else {
          process(list)
        }
    }
    
    private def process(list: List[Int]) {
      other match {
        case Some(o) =>
          worker ! Merge(list, o)
          other = None
        case None =>
          other = Some(list)
      }
    }
  }
  
  private class Worker extends Actor {
    def receive = {
      case Filter(n) =>
        queuer ! Enqueue((2 to upper).toList filter (x => x == n || x % n != 0))
      case Merge(left, right) =>
        queuer ! Enqueue(left intersect right)
    }
  }
  
  def start: List[Int] = {
    implicit val timeout = Timeout(20 seconds)
    val future = master ? Find
    Await.result(future, timeout.duration).asInstanceOf[List[Int]]
  }
}