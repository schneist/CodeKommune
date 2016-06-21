package repositories


import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticClient, IndexResult, RichSearchResponse}
import domain.{TaskList, TaskTree}
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by scsf on 03.03.2016.
  */
trait TaskRepositoryElastic  extends TaskRepositoryComponent{

  val esClient: ElasticClient


  def taskRepo = new  TaskCRUDElastic(esClient)


   class TaskCRUDElastic (esClient: ElasticClient)  extends  TaskCRUD {

    override def addChildTask(parent: TaskTree, child: TaskTree): Future[Boolean] = {
      val responseF :Future[IndexResult]  = esClient.execute {
        index into "tasks/task" fields("name" -> child.name, "parent" -> parent.name)

      }
      responseF.map {
        response => {
          response.created
        }
      }
    }

    override def deleteTask(parent: TaskTree, child: TaskTree): Future[Boolean] = {
      return Future{false}
    }
  }



  def childTaskFinder = new ChildTaskFinderElastic(esClient)

  class ChildTaskFinderElastic(esClient: ElasticClient) extends ChildTaskFinder {
    def getChildren(parent: String): Future[Seq[TaskTree]] = {
      val responseF: Future[RichSearchResponse] = esClient.execute {
        search in "tasks" -> "task" query {
          bool {
            must(
              termQuery("parent", parent)
            ) not (
              termQuery("name", parent)
              )
          }
        }
      }
      responseF.map {
        response => {
          val resJson: JsValue = Json.parse(response.original.toString)
          val d = resJson \ "hits"
          val validated = d.validate[TaskList]
          validated.get.tasks.map(t => new TaskTree(t.name, Seq.empty))
        }
      }
    }

    def addDefaultData(): Future[String] = {
      Future {
        esClient.execute(
          bulk(
            index into "tasks/task" fields("name" -> "root", "parent" -> "root"),
            index into "tasks/task" fields("name" -> "hank", "parent" -> "root"),
            index into "tasks/task" fields("name" -> "jesse", "parent" -> "hank"),
            index into "tasks/task" fields("name" -> "gus", "parent" -> "hank")
          )
        ).await
        " OK "
      }
    }
  }

}
