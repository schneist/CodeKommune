package controllers

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.javadsl.BidiFlow
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Sink, Source}
import main.scala.shared.model.{ErrorMessage, WebSocketMessage}
import play.api.http.websocket.{Message, TextMessage}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc.{Controller, WebSocket}

class TreeWebsocket () extends Controller {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()


  //internal
  /**
    * Incoming requests that change the tree always  go  only to the repository
    * Changes in the repository go to all the connected sockets
    * the outgoing message queue therefore needs to change teh flow graph for connects
    */
  private val messageQueueOut: Sink[WebSocketMessage,NotUsed] = updateOutgoingMessageQueue(Seq.empty)
  private val messageQueueIn = Source.queue(1000,OverflowStrategy.backpressure)

  implicit val materializer: Materializer = ActorMaterializer(
    ActorMaterializerSettings(system)
      .withAutoFusing(true)
      .withDebugLogging(false)
      .withSupervisionStrategy(Supervision.getResumingDecider)
  )

  //functions
  private val updateOutgoingMessageQueue: (Seq[Sink[WebSocketMessage, NotUsed]]) =>  Sink[WebSocketMessage,NotUsed] = {
    listeners => {
      Sink.fromGraph(GraphDSL.create() { implicit builder =>
        val B = builder.add(Broadcast[WebSocketMessage](listeners.size))
        SinkShape(B.in)
      })
    }
  }

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
    Flow.fromSinkAndSource(messageQueueOut,messageQueueIn)
  }
}



