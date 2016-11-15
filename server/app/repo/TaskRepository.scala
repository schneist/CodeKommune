package repo

import domain.Task

import scala.concurrent.Future

/**
  * Created by stefan.schneider on 11.10.2016.
  */
abstract class TaskRepository {

  def searchTask(query: String) : Future[List[Task]]

  def deleteTask(id: String) : Future[Boolean]

  def addTask(task:Task) : Future[Boolean]

  def getTask(id:String) :Future[Task]

}
