import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.Tcp
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import scala.concurrent.duration._
import java.net.InetSocketAddress
import scala.collection.mutable


case class ProcessedSuccessfully(topic: String)
case class ProcessNext(topic: String)


class Topics(subscriber: ActorRef) extends Actor {
  // Dictionary mapping topics to message queues
  val topics: mutable.Map[String, mutable.Queue[String]] = mutable.Map()

  def receive: Receive = {
    case AddToTopic(topic, content) =>
      if (topics.contains(topic)) {
        val queue = topics(topic)
        queue.enqueue(content)
      } else {
        val queue = mutable.Queue(content)
        topics.put(topic, queue)
      }
      println(topics)
      processIncomingMessages(topic) // Process messages for the topic

  }


  def processIncomingMessages(topic: String): Unit = {
    if (topics.contains(topic)) {
      val queue = topics(topic)
      val message = queue.dequeue()
      subscriber ! PublishMessage(topic, message) // Inform the subscriber actor

    }
  }
}