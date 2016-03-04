package services

import domain.TaskTree

/**
  * Created by scsf on 03.03.2016.
  */
trait TaskServiceComponent {
  def taskService : TaskService
  trait TaskService{
    def getTree(root:String ) : TaskTree
  }

}
