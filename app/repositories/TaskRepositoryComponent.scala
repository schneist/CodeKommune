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
}
