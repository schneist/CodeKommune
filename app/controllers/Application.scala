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
    val treeFuture:Future[TaskTree] = Future {
      val treeResult : TaskTree = new TaskTree("root",Seq.empty)
      // iterate(treeResult)
      treeResult
    }
    treeFuture.map( t => Ok(Json.toJson(t)))
  }

  /**
    * *
    * private def iterate(treeF : Future[TaskTree]):Future[TaskTree]= {
    * val sss = treeF.flatMap(tree => getLeaves(tree, Seq.empty))
    * .map(leavS => leavS.map(leav =>  {taskRepo.childTaskFinder.getChildren(leav.name).foreach(ls => leav.children.++(ls));
    * leav
    * }));
    * sss.flatMap(sq => Future{sq.exists(p => p.children.size >0)}) match {
    * *
    * }
    * *
    *
    *
    * }
    * *
    * private def getLeaves(tree:TaskTree,leaves : Seq[TaskTree]): Future[Seq[TaskTree]] ={
    * tree.children.size match{
    * case 0 => return Future{leaves.+:(tree)}
    * case _ => return Future{tree.children.++(getLeaves(tree,leaves)))}
    * }
    * }
    *
    *
    **/


}

