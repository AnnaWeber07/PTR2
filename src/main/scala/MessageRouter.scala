import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
//implements routing logic for publishing the messages
// +load balancer for different actors

// Actor for routing messages to the correct subscribers
class MessageRouter extends Actor with ActorLogging {
  var topicActors = Map.empty[String, ActorRef]

  // Create topic actors for all possible topics
  val topics = Seq("topic1", "topic2", "topic3")
  for (topic <- topics) {
    topicActors += (topic -> context.actorOf(Props[TopicActor], topic))
  }

  // Create router
  val routees = topicActors.values.map { topicActor =>
    ActorRefRoutee(topicActor)
  }.toVector
  val router = Router(RoundRobinRoutingLogic(), routees)

  override def receive: Receive = {
    case msg: Publish =>
      //val topicActor = topic
      val topic = msg.topic
      val message = msg.message
      val envelope = Message(topic, message)
      router.route(envelope, sender())
    case _ =>
      log.error("Unknown message received.")
  }
}