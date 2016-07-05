package repositories

import domain._

import scala.concurrent.Future

/**
  * Created by scsf on 03.03.2016.
  */
trait TaskRepositoryComponent {

  def childTaskFinder : ChildTaskFinder

  trait ChildTaskFinder{
    def getChildren(root: String): Future[Seq[TaskTree]]

  }

  def taskCrud:TaskCRUD

  trait TaskCRUD {
    def addChildTask(parent: TaskTree, child: TaskTree) : Future[Boolean]
    def deleteTask(parent: TaskTree, child: TaskTree) : Future[Boolean]
  }

  def taskHelpers :TaskHelpers

  trait TaskHelpers{
    def addDefaultData(): Future[String]
  }
}
