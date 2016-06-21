package controllers

import components.Components
import domain.TaskTree
import play.api.libs.json.Json._
import play.api.libs.json.{JsObject, _}
import play.api.mvc._


import scala.collection.Map
import scala.concurrent.Future


import scala.concurrent.ExecutionContext.Implicits.global

class Application(rc: Components) extends Controller{


  def index =Action { implicit request =>
    Ok(views.html.index(request.toString()))
  }

  def tree = Action {
    Ok(views.html.tree())
  }

  def treedata = Action.async {
    iterate(new TaskTree("root", Seq.empty)).map(x => Ok(Json.toJson(x)))
  }

  def addTask = Action.async {
    val parent = new TaskTree("root", Seq.empty)
    val child = new TaskTree("root", Seq.empty)
    rc.myComponent.TaskRepositoryObj.taskRepository.taskRepo.addChildTask(parent,child).map(res => Ok(res.toString))
  }

  def defaultdata = Action.async {
    taskRepo.childTaskFinder.addDefaultData().map(x => Ok(x))
  }

  def taskRepo = rc.myComponent.TaskRepositoryObj.taskRepository

  private def iterate(tree: TaskTree): Future[TaskTree] = {
    taskRepo.childTaskFinder.getChildren(tree.name)
      .flatMap(l => {
        l.size match {
          case 0 => Future {tree}
          case _ => (Future sequence l.map(subtree => iterate(subtree))).map(xx => new TaskTree(tree.name, xx))
        }
      })
  }


}

