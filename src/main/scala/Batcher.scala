import akka.actor.{Actor, ActorLogging}

// Actor for batching and sending messages to a database
class Batcher extends Actor with ActorLogging {
  override def receive: Receive = {
    case msg: Message =>
      // Batch and send message to database
      log.info(s"Message sent to database: $msg")
    case _ =>
      log.error("Unknown message received.")
  }
}