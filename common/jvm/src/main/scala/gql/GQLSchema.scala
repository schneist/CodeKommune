package gql

import model.Task
import repo.TaskRepository
import sangria.macros.derive._

import scala.concurrent.Future

object GQLSchema {

  val TaskType = deriveObjectType[TaskRepository[Future],Task]()
}
