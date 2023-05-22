//import ConnectionManager.{AddSubscriber, BroadcastMessage, ConnectionClosed}
//import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
//import akka.io.Tcp.{Bind, Bound, CommandFailed, Connected, PeerClosed, Register, Write}
//import akka.util.ByteString
//
//import scala.collection.immutable
//
//// Actor for managing server connections
//object ConnectionManager {
//  def props(settings: Settings): Props =
//    Props(new ConnectionManager(settings))
//
//  final case class ConnectionClosed(address: String, id: Int)
//  final case class BroadcastMessage(subscribers: Set[ActorRef], message: ByteString)
//  final case class AddSubscriber(id: Int, sender: ActorRef)
//}
//
//// Message case classes
//case class Publish(topic: String, message: String)
//case class Subscribe(topic: String, message: String)
//case class Unsubscribe(topic: String, message: String)
//case class Message(topic: String, message: String)
//
//case class Settings(address: String, port: Int)
//
//class ConnectionManager(settings: Settings) extends Actor with ActorLogging {
//
//  private var connections: immutable.Map[Int, ActorRef] = immutable.Map.empty
//  private var subscribers: immutable.Map[Int, ActorRef] = immutable.Map.empty
//  private var connectionCounter: Int = 0
//
//  override def receive: Receive = {
//    case command @ Bound(localAddress) =>
//      log.info(s"Message broker bound to $localAddress")
//
//    case command @ CommandFailed(_: Bind) =>
//      log.error(s"Failed to bind to ${settings.address}:${settings.port}: ${command.cause.getOrElse("").toString}")
//      context.stop(self)
//
//    case command @ Connected(remote, local) =>
//      val id = connectionCounter
//      connectionCounter += 1
//
//      val handler = context.actorOf(ConnectionActor.props(id, subscribers))
//      val connection = sender()
//      connection ! Register(handler)
//      connections = connections + (id -> connection)
//
//      log.info(s"New connection established from $remote with id $id")
//
//    case AddSubscriber(id, sender) =>
//      subscribers = subscribers + (id -> sender)
//
//    case BroadcastMessage(subscribers, message) =>
//      subscribers.foreach(_ ! Write(message))
//
//    case PeerClosed =>
//      connections.foreach { case (key, value) =>
//        if (value == sender()) {
//          context.parent ! ConnectionClosed(value.path.address.toString, key)
//          context.stop(self)
//        }
//      }
//  }
//}
//
//object ConnectionActor {
//  def props(id: Int, subscribers: Map[Int, ActorRef]): Props =
//    Props(new ConnectionActor(id, subscribers))
//
//  final case class IncomingMessage(id: Int, message: String)
//  final case class OutgoingMessage(message: ByteString)
//}
//
//class ConnectionActor(id: Int, subscribers: Map[Int, ActorRef]) extends Actor with ActorLogging {
//  import ConnectionActor._
//
//  override def receive: Receive = {
//    case data: ByteString =>
//      context.parent ! BroadcastMessage(subscribers.values.filterNot(_ == self).toSet, data)
//
//    case IncomingMessage(id, message) =>
//      log.info(s"Received message '$message' from connection $id")
//      sender() ! OutgoingMessage(ByteString(s"You said: $message"))
//
//    case OutgoingMessage(data) =>
//      sender() ! Write(data)
//
//    case PeerClosed =>
//      log.info(s"Connection $id closed by peer")
//      context.parent ! ConnectionClosed("", id)
//      context.stop(self)
//  }
//}
//
//
