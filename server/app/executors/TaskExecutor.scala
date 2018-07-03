package executors

import repo.TaskRepository

import scala.concurrent.Future

class TaskExecutor(implicit val taskRepository: TaskRepository[Future]){



}


