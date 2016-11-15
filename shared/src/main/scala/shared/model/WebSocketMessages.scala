package main.scala.shared.model

import domain.Task
import play.api.libs.json._


/**
  * Created by stefan.schneider on 10.10.2016.
  */
case class WebSocketMessage(Message: Message, requestID:String)


trait Message


trait ResponseMessage extends Message

trait CommandMessage extends Message

case class ErrorMessage(error :String) extends Message

object ErrorMessage{
  implicit val errorMessageReads = Json.reads[ErrorMessage]
  implicit val errorMessageWrites = Json.writes[ErrorMessage]
}

case class ConfirmationMessage(successful:Boolean) extends ResponseMessage

object ConfirmationMessage{
  implicit val addMessageReads = Json.reads[ConfirmationMessage]
  implicit val addMessageWrites = Json.writes[ConfirmationMessage]
}

case class TaskResultMessage(tasks:Seq[Task]) extends ResponseMessage

object TaskResultMessage{
  implicit val taskResultMessageReads = Json.reads[TaskResultMessage]
  implicit val taskResultMessageWrites = Json.writes[TaskResultMessage]
}

case class AddMessage(task:Task) extends CommandMessage

object AddMessage{
  implicit val addMessageReads = Json.reads[AddMessage]
  implicit val addMessageWrites = Json.writes[AddMessage]
}

case class SearchMessage(query:String) extends CommandMessage

object SearchMessage{
  implicit val SearchMessageReads = Json.reads[SearchMessage]
  implicit val SearchMessageWrites = Json.writes[SearchMessage]
}

case class DeleteMessage(id:String) extends CommandMessage

object DeleteMessage{
  implicit val DeleteMessageReads = Json.reads[DeleteMessage]
  implicit val DeleteMessageWrites = Json.writes[DeleteMessage]
}

case class GetMessage(id:String) extends CommandMessage

object GetMessage {
  implicit val GetMessageReads = Json.reads[GetMessage]
  implicit val GetMessageWrites = Json.writes[GetMessage]

}

object Message{
   implicit val mesaageReads:Reads[Message] = new Reads[Message] {
     override def reads(json: JsValue): JsResult[Message] = {
       (json \ "addMessage").asOpt[AddMessage] match {
         case Some(t) => return JsSuccess(t)
         case _ =>
       }
       (json \ "errorMessage").asOpt[ErrorMessage] match {
         case Some(t) => return JsSuccess(t)
         case _ =>
       }
       (json \ "deleteMessage").asOpt[DeleteMessage] match {
        case Some(t) => return JsSuccess(t)
        case _ =>
      }
      (json \ "searchMessage").asOpt[SearchMessage] match {
        case Some(t) => return JsSuccess(t)
        case _ =>
      }
      (json \ "getMessage").asOpt[GetMessage] match {
        case Some(t) => return JsSuccess(t)
        case _ =>
      }
      (json \ "ConfirmationMessage").asOpt[ConfirmationMessage] match {
        case Some(t) => return JsSuccess(t)
        case _ =>
      }
      (json \ "taskResultMessage").asOpt[TaskResultMessage] match {
        case Some(t) => return JsSuccess(t)
        case _ =>
      }
      JsError("no known command message found")
    }
  }
}


object WebSocketMessage {

  implicit val wsmWrite: Writes[WebSocketMessage] = new Writes[WebSocketMessage] {
    override def writes(sbResult: WebSocketMessage): JsValue = {
      sbResult.Message match {
        case dm :DeleteMessage => {
          Json.obj(
            "deleteMessage" -> Json.toJson(dm),
            "requestID" -> sbResult.requestID
          )
        }
        case gm :GetMessage=>  {
          Json.obj(
            "getMessage" -> Json.toJson(gm),
            "requestID" -> sbResult.requestID
          )
        }
        case sm: SearchMessage => {
          Json.obj(
            "searchMessage" -> Json.toJson(sm),
            "requestID" -> sbResult.requestID
          )
        }
        case am :AddMessage =>  {
          Json.obj(
            "addMessage" -> Json.toJson(am),
            "requestID" -> sbResult.requestID
          )
        }
        case am :ConfirmationMessage =>  {
          Json.obj(
            "confirmationMessage" -> Json.toJson(am),
            "requestID" -> sbResult.requestID
          )
        }
        case am :TaskResultMessage =>  {
          Json.obj(
            "taskResultMessage" -> Json.toJson(am),
            "requestID" -> sbResult.requestID
          )
        }
        case em:ErrorMessage => {
          Json.obj(
            "errorMessage" -> Json.toJson(em),
            "requestID" -> sbResult.requestID
          )
        }
      }
    }
  }

  implicit val WebSocketMessageReads: Reads[WebSocketMessage] = new Reads[WebSocketMessage] {
    override def reads(json: JsValue): JsResult[WebSocketMessage] = {
      val commandMessage = json.asOpt[Message] match {
        case Some(t) => t
        case None => return JsError()
      }
      val requestID = (json \ "requestID").asOpt[String] match {
        case Some(t) => t
        case None => return JsError()
      }
      val message = WebSocketMessage(Message = commandMessage, requestID = requestID)
      JsSuccess(message)
    }
  }

}