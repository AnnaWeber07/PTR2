import akka.actor.{ActorSystem, Props}

object MessageBroker extends App {
  // Create actor system
  val system = ActorSystem("message-broker")

  // Create actors
  val connectionManager = system.actorOf(Props[ConnectionManager], "connection-manager")
  val deadLetterChannel = system.actorOf(Props[DeadLetterChannel], "dead-letter-channel")
  val batcher = system.actorOf(Props[Batcher], "batcher")
  val messageRouter = system.actorOf(Props[MessageRouter], "message-router")
  val publisher = system.actorOf(Props(new Publisher(connectionManager, messageRouter)), "publisher")
  val subscriber = system.actorOf(Props(new Subscriber(connectionManager)), "subscriber")

  // Publish messages
  publisher ! Publish("topic1", "Message 1")
  publisher ! Publish("topic2", "Message 2")
  publisher ! Publish("topic3", "Message 3")

  // Subscribe to topics
  subscriber ! Subscribe("topic1")
  subscriber ! Subscribe("topic2")
  subscriber ! Subscribe("topic3")

  // Unsubscribe from topics
  subscriber ! Unsubscribe("topic1")
  subscriber ! Unsubscribe("topic2")
  subscriber ! Unsubscribe("topic3")

  // Shutdown system
  system.terminate()
}