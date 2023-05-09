import akka.actor.{Actor, ActorLogging, ActorRef}
import scala.collection.immutable.Map

class TopicActor extends Actor with ActorLogging {
  //a normal actor for dedicated topic

  //it implements:
  //receive incoming messages from a publisher
  //send the message to connection server manager
  //searches for a specific message in the database
  //persists the messages in batcher
  //searches for messages in batcher
  //if the message is dead, send the dead message to dead letter actor and receive it again

  var subscribers: Map[String, ActorRef] = Map.empty

  override def receive: Receive = {
    case Subscribe(id) =>
      subscribers = subscribers + (id -> sender())
      log.info(s"Subscriber $id subscribed to topic ${self.path.name}. Total subscribers: ${subscribers.size}")
    case Unsubscribe(id) =>
      subscribers.get(id) match {
        case Some(subscriber) =>
          subscribers = subscribers - id
          log.info(s"Subscriber $id unsubscribed from topic ${self.path.name}. Total subscribers: ${subscribers.size}")
        case None =>
          log.warning(s"Unknown subscriber $id tried to unsubscribe from topic ${self.path.name}")
      }
    case msg: Message =>
      subscribers.foreach { case (_, subscriber) =>
        subscriber ! msg
      }
    case _ =>
      log.error("Unknown message received.")
  }
}
