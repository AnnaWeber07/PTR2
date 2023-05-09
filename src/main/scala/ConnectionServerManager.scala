import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.{IO, Tcp, Udp}
import akka.util.ByteString

import scala.collection.immutable.Map

// Actor for managing server connections
class ConnectionManager extends Actor with ActorLogging {
  import Tcp._
  import context.system

  var tcpServer: Option[ActorRef] = None
  var udpServer: Option[ActorRef] = None

  var tcpConnections: Map[String, ActorRef] = Map.empty
  var udpConnections: Map[String, ActorRef] = Map.empty

  override def preStart(): Unit = {
    // Start TCP server
    tcpServer = Some(IO(Tcp) ! Tcp.Bind(self, Settings.TcpServerAddress))

    // Start UDP server
    udpServer = Some(IO(Udp) ! Udp.Bind(self, Settings.UdpServerAddress))
  }

  override def receive: Receive = {
    case Tcp.Bound(localAddress) =>
      log.info(s"TCP server bound to $localAddress")

    case Udp.Bound(localAddress) =>
      log.info(s"UDP server bound to $localAddress")

    case Tcp.CommandFailed(_: Tcp.Bind) =>
      log.error("TCP server binding failed.")
      context.stop(self)

    case Udp.CommandFailed(_: Udp.Bind) =>
      log.error("UDP server binding failed.")
      context.stop(self)

    case Tcp.Connected(remote, local) =>
      val connection = sender()
      val connectionId = s"tcp-${remote.getHostName}:${remote.getPort}"
      log.info(s"TCP connection established: $connectionId")

      val connectionActor = context.actorOf(ConnectionActor.props(connection, self))
      tcpConnections = tcpConnections + (connectionId -> connectionActor)

    case Udp.Received(data, remote) =>
      val connectionId = s"udp-${remote.getAddress.getHostAddress}:${remote.getPort}"
      udpConnections.get(connectionId) match {
        case Some(connectionActor) =>
          connectionActor ! data
        case None =>
          val connectionActor = context.actorOf(ConnectionActor.props(remote, self))
          udpConnections = udpConnections + (connectionId -> connectionActor)
          connectionActor ! data
      }

    case Udp.Unbind =>
      udpServer.foreach(_ ! Udp.Unbind)

    case Udp.Unbound =>
      log.info("UDP server unbound.")
      context.stop(self)

    case Tcp.Unbind =>
      tcpServer.foreach(_ ! Tcp.Unbind)

    case Tcp.Unbound =>
      log.info("TCP server unbound.")
      context.stop(self)

    case message: BroadcastMessage =>
      tcpConnections.values.foreach(_ ! message)
      udpConnections.values.foreach(_ ! message)

    case message: String =>
      tcpConnections.values.foreach(_ ! ByteString(message))

    case ConnectionClosed(connection) =>
      val connectionId = s"${connection.protocol}-${connection.remoteAddress}"
      if (connection.protocol == "tcp") {
        tcpConnections = tcpConnections - connectionId
      } else if (connection.protocol == "udp") {
        udpConnections = udpConnections - connectionId
      }

    case _ =>
      log.error("Unknown message received.")
  }
}

// Companion object for ConnectionManager actor
object ConnectionManager {
  def props: Props = Props(new ConnectionManager)
}
