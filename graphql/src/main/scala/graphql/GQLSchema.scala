package graphql

import model.Task
import repo.TaskRepository
import sangria.macros.derive._

object GQLSchema {

  val TaskType = deriveObjectType[TaskRepository,Task]()
}
