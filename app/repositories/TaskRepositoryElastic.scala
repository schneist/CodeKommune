package repositories

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{RichSearchResponse, ElasticClient}
import domain.{TaskTree, Task}
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by scsf on 03.03.2016.
  */
trait TaskRepositoryElastic  extends TaskRepositoryComponent{

  val esClient: ElasticClient

  def   childTaskFinder  = new ChildTaskFinderElastic(esClient)

  class ChildTaskFinderElastic(esClient: ElasticClient) extends ChildTaskFinder {
    def getChildren(parent: String): Seq[Task] = {
      val response: RichSearchResponse = esClient.execute {
        search in "tasks" -> "task" query {
          termsQuery("parent", parent)
        }
      }.await
      val resJson: JsValue = Json.parse(response.original.toString)
      val d = resJson \ "hits" \ "hits"
      d.validate[TaskList].get.tasks
    }
  }


  case class TaskList(name:String,tasks : Seq[Task])



  implicit val taskReads: Reads[Task] = (
    (JsPath \ "_id").read[String] and
      (JsPath \ "_source" \ "parent").read[String] and
      (JsPath \ "_source" \ "name").read[String]
    ) (Task.apply _)


  implicit val taskListReads: Reads[TaskList] = (
    Reads.pure("name") and
      (JsPath \ "_id").read[Seq[Task]]
    ) (TaskList.apply _)
}
