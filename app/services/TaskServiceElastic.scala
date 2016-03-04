package services

import domain.TaskTree
import repositories.TaskRepositoryComponent

/**
  * Created by scsf on 03.03.2016.
  */
trait TaskServiceElasticComponent extends TaskServiceComponent {this: TaskRepositoryComponent =>

  def taskService = new DefaultTaskService

  class DefaultTaskService extends TaskService{
    override def getTree(root: String): TaskTree ={

      return new TaskTree("dummy",Seq.empty)
    }
  }

}
