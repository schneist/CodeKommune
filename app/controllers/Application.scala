package controllers


import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{RichSearchResponse, ElasticClient}
import domain._
import play.api.mvc._

import play.api.libs.json._
import repositories.Services

import scala.annotation.tailrec

// JSON library
import play.api.libs.json.Reads._

// Custom validation helpers
import play.api.libs.functional.syntax._

// Combinator syntax
import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global


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

    val parents : Seq[String] = getLeaves(tree,Seq.empty);
    parents.map(p => taskService.childTaskFinder.getChildren(p))

    return new TaskTree("",Seq.empty)
  }

  private def getLeaves(tree: TaskTree,leaves : Seq[String]): Seq[String] ={
    tree.children.size match{
      case 0 => leaves.+:(tree.name)
      case _ => tree.children.flatMap( l => getLeaves(l,leaves))
    }
  }

  implicit lazy val treeWrites: Writes[TaskTree] = (
    (JsPath\ "name").write[String] and
      (JsPath \ "children").lazyWrite(Writes.seq[TaskTree](treeWrites))
    )(unlift(TaskTree.unapply))




}

