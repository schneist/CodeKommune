package controllers

import components.Components
import domain.TaskTree
import play.api.libs.json._
import play.api.mvc._

import scala.annotation.tailrec


// Combinator syntax
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class Application(rc: Components) extends Controller {


  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def tree = Action {
    Ok(views.html.tree())
  }

  def treedata = Action.async {
    val treeFuture:Future[TaskTree] = Future {
      val treeResult : TaskTree = new TaskTree("root",Seq.empty)
      iterate(treeResult)
    }
    treeFuture.map( t => Ok(Json.toJson(t)))
  }

  @tailrec
  private def iterate(tree : TaskTree): TaskTree ={
    var ret = tree
    val size = tree.children.size
    val parents : Seq[String] = getLeaves(tree,Seq.empty);
    parents.foreach(p => ret = ret.addChildren(p, rc.myComponent.TaskRepositoryObj.taskRepository.childTaskFinder.getChildren(p)))
    if (ret.children.size == size) {
      return ret
    }
    iterate(ret)
  }

  private def getLeaves(tree: TaskTree,leaves : Seq[String]): Seq[String] ={
    tree.children.size match{
      case 0 => leaves.+:(tree.name)
      case _ => tree.children.flatMap( l => getLeaves(l,leaves))
    }
  }






}

