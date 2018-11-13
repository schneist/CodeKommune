package repo

import java.util.UUID

import com.sksamuel.elastic4s.http.HttpClient
import com.sksamuel.elastic4s.{Indexable, RefreshPolicy}
import play.api.libs.json.Json
import model._

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by stefan.schneider on 11.10.2016.
  */

class InMemoryTaskRepo( implicit val ec:ExecutionContext) extends TaskRepository[Future] {

  val taskList = new mutable.HashMap[String,Task]()

  override def searchTask(query: String): Future[Seq[Task]] = Future.apply{
    taskList.values.filter(_.name.contains(query)).toSeq
  }

  override def deleteTask(id: String): Future[Unit] = Future.apply(taskList.remove(id))

  override def addTask(task: Task): Future[Task] = Future.apply {
    val taskWithID = task.copy(id = Some(task.id.getOrElse(UUID.randomUUID().toString)))
    taskList.put(taskWithID.id.get, taskWithID)
    taskWithID
  }

  override def getTask(id: String): Future[Option[Task]] = Future.apply(taskList.get(id))
}

class ElasticClientTaskRepository(val indexName:String,implicit val httpClient: HttpClient,implicit val ec:ExecutionContext) extends TaskRepository[Future]{
  import com.sksamuel.elastic4s.http.ElasticDsl._

  override def searchTask(querys: String): Future[Seq[Task]] = {
   httpClient.execute {
      search("artists") query querys
    }.flatMap(a => a match{
      case Left(e) => Future.failed(new Exception(e.toString))
      case Right(res) => Future.successful(
        res.result
      )
    })
    .map(a => {
       a.hits.hits.map(h => new Task(name= h.fields.getOrElse("name","").toString))
    })
  }

  override def deleteTask(id: String): Future[Unit] = {
    httpClient.execute {
      delete("u2").from("bands/rock")
    }.flatMap(a => a match{
      case Left(e) => Future.failed(new Exception(e.toString))
      case Right(_)  => Future.successful(():Unit)
    })

  }

  override def addTask(task: Task): Future[Task] = {
   ???
  }

  override def getTask(id: String): Future[Option[Task]] = ???

  def initIndex ={
    httpClient.execute(
      createIndex(indexName).mappings(
        mapping("task").fields(
          textField("name"),
          textField("parent")
        )
      )
    )
  }

  implicit object TaskIndexable extends Indexable[Task] {
    override def json(t: Task): String = Json.toJson(t).toString()
  }


  def fillTestdata={

    httpClient.execute {
      indexInto(indexName / "task")
        .doc(Task(name = "clean kitchen"))
        .doc(Task(name =  "refill toilet paper"))
        .doc(Task(name = "refill printer paper"))
        .doc(Task(name = "buy toilet paper"))
        .doc(Task(name = "buy printer paper"))
        .refresh(RefreshPolicy.IMMEDIATE)
    }

  }
}