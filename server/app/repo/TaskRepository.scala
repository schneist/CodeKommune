package repo

import java.util.UUID

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
