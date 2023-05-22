//import akka.actor.ProviderSelection.{local, remote}
//
//import java.net.InetSocketAddress
//import java.nio.charset.StandardCharsets
//import java.util.UUID
//import akka.actor.{Actor, ActorLogging, ActorRef}
//import akka.io.Tcp.{Bound, CommandFailed, Connected, Received}
//import akka.io.{IO, Tcp}
//import akka.util.ByteString
//
//
//class TCPServerManager(address: String, port: Int) extends Actor with ActorLogging {
//  implicit val system = context.system IO (Tcp) ! Tcp.Bind(self, new InetSocketAddress(address, port))
//
//
//  override def receive: Receive = {
//    case Bound(localAddress) =>
//      log.info(s"Server started on ${localAddress}")
//    case Connected(remote, local) =>
//      log.info(s"Client connected from ${remote} to ${local}")
//      sender() ! Tcp.Register(self)
//    case Received(data) =>
//      val dataString = data.decodeString(StandardCharsets.UTF_8)
//      log.info(s"Received data: ${dataString}")
//      val sender = context.sender()
//
//  }
//}
//class Client(address: String, port: Int) extends Actor with ActorLogging {
//  implicit val system = context.system
//  IO(Tcp) ! Tcp.Connect(new InetSocketAddress(address, port))
//
//  private var broker: ActorRef =
//  val messageRegex = """(\w+-\w+-\w+-\w+-\w+): (.+)""".r
//
//  override def receive: Receive = {
//    case CommandFailed(: Tcp.Connect) =>
//    log.info("Connection failed")
//    //      context.stop(self)
//    case c @ Connected(remote, local) =>
//    log.info(s"Connected to ${remote}")
//    broker = sender()
//    broker ! Tcp.Register(self)
//    context.become {
//      case data: ByteString =>
//        log.info(s"Sending data: ${data}")
//        broker ! Tcp.Write(data)
//      case Received(data) =>
//        val dataString = data.utf8String
//        log.info(s"Received data: ${dataString}")
//        dataString match {
//          case messageRegex(uuid, message) =>
//            log.info(s"Received message: ${message} from ${uuid}")
//            log.info("Sending acknowledgment to server")
//            val acknowledge = Acknowledge(UUID.fromString(uuid))
//            val ack = ByteString(acknowledge.toString)
//            broker ! Tcp.Write(ack)
//          case  =>
//            log.info("Received unknown message")
//        }
//
//      case : Tcp.ConnectionClosed =>
//      log.info("Connection closed")
//      //          context.stop(self)
//    }
//  }
//}