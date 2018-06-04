package repo
import model.Task
import scala.concurrent.Future

/**
  * Created by stefan.schneider on 11.10.2016.
  */
trait TaskRepository {

  def searchTask(query: String) : Future[Seq[Task]]

  def deleteTask(id: String) : Future[Boolean]

  def addTask(task:Task) : Future[Task]

  def getTask(id:String) :Future[Task]

}