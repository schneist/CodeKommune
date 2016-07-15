package controllers

import com.mohiva.play.silhouette.api.{Environment, HandlerResult, Silhouette}
import com.mohiva.play.silhouette.api.actions.{DefaultSecuredAction, SecuredAction, UnsecuredAction, UserAwareAction}
import components.Components
import domain.TaskTree
import play.api.libs.json._
import play.api.mvc._
import repositories.TaskRepositoryComponentElastic

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Application(rc: Components,sil:Silhouette[KommunardEnv]) extends Controller {


  val taskService = rc.repositoryComponent.TaskRepositoryObj.taskRepository

  def index =Action.async{ implicit request =>
    sil.SecuredRequestHandler { securedRequest =>
      Future.successful(HandlerResult(Ok, Some(securedRequest.identity)))
    }.map {
      case HandlerResult(r, Some(user)) => Ok(Json.toJson(request.toString()))
      case HandlerResult(r, None) => Unauthorized
    }
  }

  def tree = Action {
    Ok(views.html.tree())
  }

  def treedata = Action.async {
    iterate(new TaskTree("root", Seq.empty)).map(x => Ok(Json.toJson(x)))
  }

  def addTask = Action.async { implicit request =>
    val parent = new TaskTree("root", Seq.empty)
    val child = new TaskTree(request.getQueryString("name").getOrElse(""), Seq.empty)
    taskService.taskCrud.addChildTask(parent,child).map(res => Ok(res.toString))
  }

  def defaultdata = Action.async {
    taskService.taskHelpers.addDefaultData().map(x => Ok(x))
  }


  private def iterate(tree: TaskTree): Future[TaskTree] = {
    taskService.childTaskFinder.getChildren(tree.name)
      .flatMap(l => {
        l.size match {
          case 0 => Future {tree}
          case _ => (Future sequence l.map(subtree => iterate(subtree))).map(xx => new TaskTree(tree.name, xx))
        }
      })
  }

}

