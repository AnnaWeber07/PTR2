import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.Tcp
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString

import java.net.InetSocketAddress
import scala.collection.mutable

import akka.actor.{Actor, ActorRef}
import scala.collection.mutable

case class Subscribe(topic: String, subscriber: ActorRef)
case class Unsubscribe(topic: String, subscriber: ActorRef)
case class PublishMessage(topic: String, content: String)
case class SendMessage(subscriber: ActorRef, content: String)
import scala.concurrent.duration._

class Subscriber extends Actor {
  // Dictionary mapping topic to a set of subscribers
  var uniqueSubscribersMap: mutable.Map[String, Set[ActorRef]] = mutable.Map()

  def receive: Receive = {
    case Subscribe(topic, subscriber) =>
      if (uniqueSubscribersMap.contains(topic)) {
        val subscribers = uniqueSubscribersMap(topic)
        uniqueSubscribersMap += (topic -> (subscribers + subscriber))
        subscriber ! Write(ByteString(s"Successfully subscribed!\n"))

      } else {
        uniqueSubscribersMap += (topic -> Set(subscriber))
        subscriber ! Write(ByteString(s"Successfully subscribed!\n"))
      }


    case Unsubscribe(topic, subscriber) =>
      if(uniqueSubscribersMap.contains(topic)) {
        val subscribers = uniqueSubscribersMap(topic)
        if (subscribers.contains(subscriber))
        {uniqueSubscribersMap += (topic -> (subscribers - subscriber))
          subscriber ! Write(ByteString(s"Unsubscribed\n"))}
        else subscriber ! Write(ByteString(s"Error: no subscription found\n"))
      }
      else subscriber ! Write(ByteString(s"Error: no topic found\n"))


    case PublishMessage(topic, content) =>

      if (uniqueSubscribersMap.contains(topic)) {

        val subscribers = uniqueSubscribersMap(topic)
        subscribers.foreach { subscriber =>
          subscriber ! Write(ByteString(s"$content\n"))
        }
      }
  }
}
