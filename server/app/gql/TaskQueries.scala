package gql

import repo.TaskRepository
import sangria.schema._
import sangria.relay._

object TaskQueries {
  import gql.GQLSchema._
  val namesArgument = Argument("names", ListInputType(StringType))

  val QueryType = ObjectType("Query",
    fields[TaskRepository, Unit](
      Field(name= "task",
        fieldType =ListType(TaskType),
        description = Some("Returns all mathcing Tasks"),
        arguments = namesArgument :: Nil,
        resolve = c ⇒ c.ctx.searchTask(c.arg(namesArgument.name))
      )
    )
  )
  val schema = Schema(QueryType)
}
