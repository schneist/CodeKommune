package controllers

import actors.UserActor
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.javadsl.BidiFlow
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import main.scala.shared.model.{ErrorMessage, WebSocketMessage}
import org.reactivestreams.Publisher
import play.api.http.websocket.{Message, TextMessage}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc.{Controller, WebSocket}

class TreeWebsocket () extends Controller {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  def infunc: (Message) => WebSocketMessage = {
    case tm:TextMessage =>
      Json.parse(tm.data).validate[WebSocketMessage] match{
        case JsSuccess(wsm,_) => wsm
        case JsError(err) => WebSocketMessage(ErrorMessage(err.mkString),"-1")
      }
    case _ =>   WebSocketMessage(ErrorMessage("Can only accept TextMessages"),"-1")
  }

  def outfunc: (WebSocketMessage) => Message = {
    wsm => TextMessage(Json.toJson(wsm).toString())
  }

  implicit val transformer: MessageFlowTransformer[WebSocketMessage,WebSocketMessage] =
    new MessageFlowTransformer[WebSocketMessage,WebSocketMessage] {
      override def transform(flow: Flow[WebSocketMessage, WebSocketMessage, _]): Flow[Message, Message, _] = flow.join(BidiFlow.fromFlows(Flow.fromFunction(outfunc),Flow.fromFunction(infunc)))
    }

  def treesocket: WebSocket = WebSocket.accept[WebSocketMessage, WebSocketMessage] { requestHeader =>
    val userActorSource: Source[WebSocketMessage, ActorRef] = Source.actorRef[WebSocketMessage](10, OverflowStrategy.backpressure)
    val (webSocketOut: ActorRef, webSocketIn: Publisher[WebSocketMessage]) = userActorSource.toMat(Sink.asPublisher(fanout = false))(Keep.both).run()
    val userActor: ActorRef = system.actorOf(Props(classOf[UserActor], webSocketOut), requestHeader.remoteAddress)
    Flow.fromSinkAndSource(Sink.actorRef(userActor, akka.actor.Status.Success(())), Source.fromPublisher(webSocketIn))
  }
}



