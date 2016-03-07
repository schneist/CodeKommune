package repositories

import domain._

/**
  * Created by scsf on 03.03.2016.
  */
trait TaskRepositoryComponent {

  def childTaskFinder : ChildTaskFinder

  trait ChildTaskFinder{
    def getChildren(root :String) : Seq[Task]

  }
}
