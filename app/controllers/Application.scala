package controllers

import components.Components
import domain.TaskTree
import play.api.libs.json._
import play.api.mvc._


// Combinator syntax
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class Application(rc: Components) extends Controller {

  def taskRepo = rc.myComponent.TaskRepositoryObj.taskRepository

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def tree = Action {
    Ok(views.html.tree())
  }

  def treedata = Action.async {
    iterate(new TaskTree("root", Seq.empty)).map(t => Ok(Json.toJson(t)))
  }

  private def iterate(tree: TaskTree): Future[TaskTree] = {
    taskRepo.childTaskFinder.getChildren(tree.name).map(l => {
      tree.children.size match {
        case 0 => new TaskTree(tree.name, l)
        case _ => return iterate(new TaskTree(tree.name, l))
      }
    }
    )
  }

}

