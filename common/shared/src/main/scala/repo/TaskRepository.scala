package repo
import model.Task
import scala.concurrent.Future

/**
  * Created by stefan.schneider on 11.10.2016.
  */
trait TaskRepository[F[_]] {

  def searchTask(query: String) : F[Seq[Task]]

  def deleteTask(id: String) : F[Unit]

  def addTask(task:Task) : F[Task]

  def getTask(id:String) :F[Option[Task]]

}