package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import components.Components
import play.api.libs.streams.ActorFlow
import play.api.mvc._

class TreeWebsocket(rc: Components) extends Controller {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  val taskService = rc.repositoryComponent.TaskRepositoryObj.taskRepository

  def treesocket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => MyWebSocketActor.props(out))
  }


  object MyWebSocketActor {
    def props(out: ActorRef) = Props(new MyWebSocketActor(out))
  }

  class MyWebSocketActor(out: ActorRef) extends Actor {
    def receive = {
      case msg: String =>
        out ! ("I received your message: " + msg)
    }
  }


}

