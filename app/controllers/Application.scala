package controllers


import domain._
import play.api.libs.json._
import play.api.mvc._
import repositories.Services


// Combinator syntax
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class Application   extends Controller {

  val taskService = Services.TaskServiceObj.taskServiceComponent

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def tree = Action {
    Ok(views.html.tree().toString())
  }

  def treedata = Action.async {
    val treeFuture:Future[TaskTree] = Future {
      val treeResult : TaskTree = new TaskTree("root",Seq.empty)
      iterate(treeResult)
    }
    treeFuture.map( t => Ok(Json.toJson(t)))
  }

  private def iterate(tree : TaskTree): TaskTree ={
    var ret = tree
    val parents : Seq[String] = getLeaves(tree,Seq.empty);
    parents.foreach(p => ret = ret.addChildren(p, taskService.childTaskFinder.getChildren(p)))
    ret
  }

  private def getLeaves(tree: TaskTree,leaves : Seq[String]): Seq[String] ={
    tree.children.size match{
      case 0 => leaves.+:(tree.name)
      case _ => tree.children.flatMap( l => getLeaves(l,leaves))
    }
  }






}

