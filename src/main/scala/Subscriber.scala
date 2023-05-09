import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

// Actor for the subscriber
class Subscriber(connectionManager: ActorRef) extends Actor with ActorLogging {
  //receives a topic message from connection server manager

  implicit val timeout: Timeout = 3 seconds

  // Register with connection manager as a consumer
  connectionManager ! "register_consumer"

  override def receive: Receive = {
    case Subscribe(topic) =>
      // Find producer for topic
      val producers = Await.result(connectionManager ? "get_producers", timeout.duration).asInstanceOf[Set[ActorRef]]
      val producer = producers.headOption.getOrElse(context.system.deadLetters)
      // Subscribe to topic
      producer ! Subscribe(topic)

    case Unsubscribe(topic) =>
      // Find producer for topic
      val producers = Await.result(connectionManager ? "get_producers", timeout.duration).asInstanceOf[Set[ActorRef]]
      val producer = producers.headOption.getOrElse(context.system.deadLetters)

      // Unsubscribe from topic
      producer ! Unsubscribe(topic)
    case msg: Message =>
      log.info(s"Received message: $msg")
    case _ =>
      log.error("Unknown message received.")
  }

  override def postStop(): Unit = {
    // Unregister with connection manager as a consumer
    connectionManager ! "unregister_consumer"
  }
}