package controllers


import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{RichSearchResponse, ElasticClient}
import domain._
import play.api.mvc._

import play.api.libs.json._

import scala.annotation.tailrec

// JSON library
import play.api.libs.json.Reads._

// Custom validation helpers
import play.api.libs.functional.syntax._

// Combinator syntax
import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global


class Application   extends Controller {

  val taskService = ElasticClient.transport("elasticsearch://localhost:9300")

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

    val parents : Seq[String] = getLeaves(tree,Seq.empty);
    val response: RichSearchResponse = client.execute {
      search in "ck"->"task" query { termsQuery("parent", parents.mkString(",")) }
    }.await

    val resJson: JsValue  = Json.parse(response.original.toString)
    val d = resJson \ "hits" \"hits"
    val taskList: TaskList = d.validate[TaskList].get
    val trees : Seq[TaskTree] = taskList.tasks.map(t => new TaskTree(t.name, Seq.empty))
    iterate(new TaskTree(tree.name, tree.children ++ trees))
  }

  private def getLeaves(tree: TaskTree,leaves : Seq[String]): Seq[String] ={
    tree.children.size match{
      case 0 => leaves.+:(tree.name)
      case _ => tree.children.flatMap( l => getLeaves(l,leaves))
    }
  }


  case class TaskList(name:String,tasks : Seq[Task])



  implicit val taskReads: Reads[Task] = (
    (JsPath \ "_id").read[String] and
      (JsPath \ "_source" \ "parent").read[String] and
      (JsPath \ "_source" \ "name").read[String]
    ) (Task.apply _)

  implicit lazy val treeWrites: Writes[TaskTree] = (
    (JsPath\ "name").write[String] and
      (JsPath \ "children").lazyWrite(Writes.seq[TaskTree](treeWrites))
    )(unlift(TaskTree.unapply))

  implicit val taskListReads: Reads[TaskList] = (
    Reads.pure("name") and
      (JsPath \ "_id").read[Seq[Task]]
    ) (TaskList.apply _)


}

