package gql

import model.Task
import repo.TaskRepository
import sangria.schema._
import sangria.relay._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object TLSchema {
  val namesArgument = Argument("names", StringType)

  val QueryType = ObjectType("Query",
    fields[TaskRepository[Future], Unit](
      Field(name= "task",
        fieldType =ListType(GQLSchema.TaskType),
        description = Some("Returns all matching Tasks"),
        arguments = namesArgument :: Nil,
        resolve = c ⇒ c.ctx.searchTask(c.arg(namesArgument.name))
      )
    )
  )

  case class taskMutationPayload(clientMutationId: Option[String], taskId: String, taskName: String) extends Mutation

  val taskMutation = Mutation.fieldWithClientMutationId[TaskRepository[Future], Unit, taskMutationPayload, InputObjectType.DefaultInput](
    fieldName = "addTask",
    typeName = "AddTask",
    inputFields = List(
      InputField("taskName", StringType)),
    outputFields = fields(
      Field("task", OptionType(GQLSchema.TaskType), resolve = ctx ⇒ ctx.ctx.getTask(ctx.value.taskId))),
    mutateAndGetPayload = (input, ctx) ⇒ {
      val mutationId = input.get(Mutation.ClientMutationIdFieldName).asInstanceOf[Option[Option[String]]].flatten
      val taskName = input("taskName").asInstanceOf[String]

      val newTask  :Future[Task] = ctx.ctx.addTask(new Task(name= taskName))
      taskMutationPayload(mutationId, Await.result(newTask, 1 second).id.getOrElse(""), taskName)
    }
  )

  val MutationType = ObjectType("taskMutation", fields[TaskRepository[Future], Unit](taskMutation))

  val tlschema :Schema[TaskRepository[Future], Unit] =  Schema(QueryType, Some(MutationType))
}
