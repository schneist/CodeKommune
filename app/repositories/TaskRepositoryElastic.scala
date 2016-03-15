package repositories

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticClient, RichSearchResponse}
import domain.{TaskList, TaskTree}
import play.api.libs.json._

/**
  * Created by scsf on 03.03.2016.
  */
trait TaskRepositoryElastic  extends TaskRepositoryComponent{

  val esClient: ElasticClient

  def childTaskFinder = new ChildTaskFinderElastic(esClient)

  class ChildTaskFinderElastic(esClient: ElasticClient) extends ChildTaskFinder {
    def getChildren(parent: String): Seq[TaskTree] = {

      val req = search in "tasks" -> "task" query {
        bool {
          must(
            termQuery("parent", parent)
          )
          not(
            termQuery("name", parent)
          )
        }
      }
      val response: RichSearchResponse = esClient.execute {
          req
      }.await
      val resJson: JsValue = Json.parse(response.original.toString)
      val d = resJson \ "hits"
      val validated = d.validate[TaskList]
      validated.get.tasks.map(t => new TaskTree(t.name, Seq.empty))
    }
  }


}
