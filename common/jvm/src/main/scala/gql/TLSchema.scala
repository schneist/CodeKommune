package gql

import repo.TaskRepository
import sangria.schema._
import sangria.relay._

object TLSchema {
  val namesArgument = Argument("names", StringType)

  val QueryType = ObjectType("Query",
    fields[TaskRepository, Unit](
      Field(name= "task",
        fieldType =ListType(GQLSchema.TaskType),
        description = Some("Returns all matching Tasks"),
        arguments = namesArgument :: Nil,
        resolve = c ⇒ c.ctx.searchTask(c.arg(namesArgument.name))
      )
    )
  )

  val tlschema :Schema[TaskRepository, Unit] = Schema(QueryType)
}
