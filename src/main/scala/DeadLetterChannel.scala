import akka.actor.{Actor, ActorLogging}

// Actor for handling messages that cannot be delivered to a subscriber
class DeadLetterChannel extends Actor with ActorLogging {
  //receives dead letters from a topic actor, re-sends them back to the topic actor if needed

  override def receive: Receive = {
    case msg =>
      log.error(s"Message not delivered: $msg")
  }
}