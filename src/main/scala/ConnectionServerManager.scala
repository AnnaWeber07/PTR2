
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString

import java.net.InetSocketAddress

// Message case classes
case class Publish(topic: String, message: String)
case class Subscribe(topic: String)
case class Unsubscribe(topic: String)
case class Message(topic: String, message: String)


// Actor for managing connections
class ConnectionManager extends Actor with ActorLogging {
  var producers = Set.empty[ActorRef]
  var consumers = Set.empty[ActorRef]

  override def receive: Receive = {
    case "get_producers" =>
      sender() ! producers
    case "get_consumers" =>
      sender() ! consumers
    case "register_producer" =>
      producers += sender()
    case "register_consumer" =>
      consumers += sender()
    case "unregister_producer" =>
      producers -= sender()
    case "unregister_consumer" =>
      consumers -= sender()
    case _ =>
      log.error("Unknown message received.")
  }
}
