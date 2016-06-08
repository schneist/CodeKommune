package controllers

import components.Components
import domain.TaskTree
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future


// Combinator syntax
import scala.concurrent.ExecutionContext.Implicits.global

class Application(rc: Components) extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def tree = Action {
    Ok(views.html.tree())
  }

  def treedata = Action.async {
    iterate(new TaskTree("root", Seq.empty)).map(x => Ok(Json.toJson(x)))
  }

  def defaultdata = Action.async {
    taskRepo.childTaskFinder.addDefaultData().map(x => Ok(x))
  }

  def taskRepo = rc.myComponent.TaskRepositoryObj.taskRepository

  private def iterate(tree: TaskTree): Future[TaskTree] = {
    taskRepo.childTaskFinder.getChildren(tree.name)
      .flatMap(l => {
        l.size match {
          case 0 => Future {
            tree
          }
          case _ => (Future sequence l.map(subtree => iterate(subtree))).map(xx => new TaskTree(tree.name, xx))
        }
      })
  }


}

