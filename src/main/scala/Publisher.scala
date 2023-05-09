import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

// Actor for the publisher
class Publisher(connectionManager: ActorRef, messageRouter: ActorRef) extends Actor with ActorLogging {
  //publishes messages to connection server manager

  implicit val timeout: Timeout = 3 seconds

  // Register with connection manager as a producer
  connectionManager ! "register_producer"

  override def receive: Receive = {
    case msg: Publish =>
      messageRouter ! msg
    case _ =>
      log.error("Unknown message received.")
  }

  override def postStop(): Unit = {
    // Unregister with connection manager as a producer
    connectionManager ! "unregister_producer"
  }
}