import akka.actor.{Actor, ActorLogging, Props}
import akka.actor.DeadLetter

class DeadLetters extends Actor with ActorLogging {
  var list: List[Any] = List.empty

  def receive: Receive = {

    case  deadLetter: DeadLetter =>
      log.warning("Dead Letter Encountered: {}", deadLetter.message)
      list = list :+ deadLetter.message
    case dead: Any =>
      log.info(s"Dead Message: $dead")
      list = list :+ dead
  }
}
