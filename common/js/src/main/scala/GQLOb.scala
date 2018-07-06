import com.apollographql.scalajs._
import model.Task

object  GQLOb {
  object TaskMutation extends GraphQLMutation {
     val operationString =
      """
        |mutation add($input :AddTaskInput!) {
        |  addTask(input: $input){
        |    task{name}
        |    clientMutationId
        |  }
        |}
      """.stripMargin
    override val operation = com.apollographql.scalajs.gql(operationString)
  }
}

object TaskQuery extends com.apollographql.scalajs.GraphQLQuery {
  val operationString =
    """
      |query{
      |  task(names: $namesquery){
      |    id
      |    name
      |  }
      |}
    """.stripMargin

  val operation = com.apollographql.scalajs.gql(operationString)


  case class Variables(namesquery: String) {
  }

  case class Data(tasks: Option[Seq[Task]]) {
  }

  object Data {
    val possibleTypes = scala.collection.Set("Query")
  }

}