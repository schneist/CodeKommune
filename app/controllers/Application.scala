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

  /*
    @tailrec
    private def iterate(tree : Future[TaskTree]):Future[TaskTree]= {
      tree.flatMap(t => getLeaves(t, Seq.empty))

       .map(p => (p, taskRepo.childTaskFinder.getChildren(p.name))).
        map(p => p._2.map( s => p._1.children.++(s).size))
      .exists(fint => fint  >=0).)
      iterate(tree)

    }

    private def getLeaves(tree: TaskTree,leaves : Seq[TaskTree]): Future[Seq[TaskTree]] ={
      tree.children.size match{
        case 0 => Future{leaves.+:(tree)}
        case _ => tree.children.flatMap( l =>  getLeaves(l,leaves).c)
      }
    }
  */





}

