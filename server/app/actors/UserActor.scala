package actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import main.scala.shared.model._
import repo.TaskRepository

import scala.concurrent.ExecutionContext

/**
  * Created by stefan.schneider on 20.09.2016.
  */
class UserActor (out: ActorRef,repository: TaskRepository,implicit val ec: ExecutionContext) extends Actor with ActorLogging {

  def sendResponse(b: ResponseMessage, messageID: String) = out ! ( b)

  override def receive: Receive = {
    //ToDo:: incoming JSON needs to be validated
    case message: WebSocketMessage => {
      message.Message match {
        case add: AddMessage => {
          repository.addTask(add.task).map(r => sendResponse(ConfirmationMessage(r), message.requestID))
        }
        case del: DeleteMessage => {
          repository.deleteTask(del.id).map(r => sendResponse(ConfirmationMessage(r), message.requestID))
        }
        case search: SearchMessage => {
          repository.searchTask(search.query).map(r=>sendResponse(TaskResultMessage(r), message.requestID))
        }
        case get: GetMessage => {
          repository.getTask(get.id).map(r=> sendResponse(TaskResultMessage(Seq(r)), message.requestID))
        }
      }
    }
    case _ => {}
  }
}
