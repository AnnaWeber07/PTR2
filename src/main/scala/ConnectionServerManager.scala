import ConnectionManager.{AddSubscriber, BroadcastMessage, ConnectionClosed}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.Tcp.{Bind, Bound, CommandFailed, Connected, PeerClosed, Register, Write}
import akka.util.ByteString

import scala.collection.immutable


// Actor for managing server connections
object ConnectionManager {
  def props(settings: Settings): Props =
    Props(new ConnectionManager(settings))

  final case class ConnectionClosed(address: String, id: Int)
  final case class BroadcastMessage(subscribers: Set[ActorRef], message: ByteString)
  final case class AddSubscriber(id: Int, sender: ActorRef)
}

case class Settings(address: String, port: Int)

class ConnectionManager(settings: Settings) extends Actor with ActorLogging {

  val settings = Settings("localhost", 8080)
  val connectionManager = system.actorOf(ConnectionManager.props(settings), "connectionManager")

  private var connections: immutable.Map[Int, ActorRef] = immutable.Map.empty
  private var subscribers: immutable.Map[Int, ActorRef] = immutable.Map.empty
  private var connectionCounter: Int = 0

  override def receive: Receive = {
    case command @ Bound(localAddress) =>
      log.info(s"Message broker bound to $localAddress")

    case command @ CommandFailed(_: Bind) =>
      log.error(s"Failed to bind to ${settings.address}:${settings.port}: ${command.failureMessage}")
      context.stop(self)

    case command @ Connected(remote, local) =>
      val id = connectionCounter
      connectionCounter += 1

      val handler = context.actorOf(ConnectionActor.props(id, subscribers))
      val connection = sender()
      connection ! Register(handler)
      connections = connections + (id -> connection)

      log.info(s"New connection established from $remote with id $id")

    case AddSubscriber(id, sender) =>
      subscribers = subscribers + (id -> sender)

    case BroadcastMessage(subscribers, message) =>
      subscribers.foreach(_ ! Write(message))

    case PeerClosed =>
      context.parent ! ConnectionClosed(connection.remoteAddress.toString, id)
      context.stop(self)
  }
}


object ConnectionActor {
  def props(connection: ActorRef) = Props(new ConnectionActor(connection))

  case class Init(id: Int, subscribers: Map[Int, ActorRef])
}

class ConnectionActor(connection: ActorRef) extends Actor with ActorLogging {
  import ConnectionActor._

  // initialize connection id and subscriber map
  var id: Int = _
  var subscribers: Map[Int, ActorRef] = _

  override def receive: Receive = {

    // initialize connection
    case Init(connectionId, subscriberMap) =>
      id = connectionId
      subscribers = subscriberMap

    // handle incoming data
    case data: ByteString =>
      val message = s"[id=$id] $data"
      log.info(s"Received message: ")
        BroadcastMessage(subscribers.values.filterNot(_ == self).toSet, ByteString(message)))

    // handle connection closed
    case PeerClosed =>
      context.parent ! ConnectionClosed(connection.remoteAddress.toString, id)
      context.stop(self)
  }
}