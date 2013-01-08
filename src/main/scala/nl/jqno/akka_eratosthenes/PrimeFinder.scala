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
import scala.collection.mutable.HashMap

object PrimeFinder {
  type ActorRefs = collection.mutable.Map[String, ActorRef]
  
  class Master(upper: Int, actors: ActorRefs) extends Actor {
    var source: ActorRef = _
    
    def receive = {
      case Find =>
        source = sender
        (2 to upper) foreach { n => actors("worker") ! Filter(n) }
      case Result(list) =>
        source ! list
    }
  }
  
  class Queuer(upper: Int, actors: ActorRefs) extends Actor {
    private var other: Option[List[Int]] = None
    private var expectedMessages = ((upper - 1) * 2) - 1
    
    def receive = {
      case Enqueue(list) =>
        expectedMessages -= 1
        if (expectedMessages == 0) {
          actors("master") ! Result(list)
        }
        else {
          process(list)
        }
    }
    
    private def process(list: List[Int]) {
      other match {
        case Some(o) =>
          actors("worker") ! Merge(list, o)
          other = None
        case None =>
          other = Some(list)
      }
    }
  }
  
  class Worker(upper: Int, actors: ActorRefs) extends Actor {
    def receive = {
      case Filter(n) =>
        actors("queuer") ! Enqueue((2 to upper).toList filter (x => x == n || x % n != 0))
      case Merge(left, right) =>
        actors("queuer") ! Enqueue(left intersect right)
    }
  }
  
  def apply(upper: Int, nrOfWorkers: Int = 2): List[Int] = {
    if (upper < 2)
      Nil
    else {
      val actors = new HashMap[String, ActorRef]
      val system = ActorSystem("EratosthenesSystem")
      
      val master = system.actorOf(Props(new Master(upper, actors)), name = "master")
      val queuer = system.actorOf(Props(new Queuer(upper, actors)), name = "queuer")
      val worker = system.actorOf(Props(new Worker(upper, actors)) withRouter RoundRobinRouter(nrOfWorkers), name = "worker")
      
      actors += ("master" -> master, "queuer" -> queuer, "worker" -> worker)
  
      implicit val timeout = Timeout(20 seconds)
      val future = master ? Find
      Await.result(future, timeout.duration).asInstanceOf[List[Int]]
    }
  }
}