package shared.model
import play.api.libs.json.Json


case class Task(
                 id: Option[String] = None,
                 parent: Option[String] = None ,
                 name: String,
                 creationUser: Option[String] = None,
                 assignedUser : Option[String] = None
               )

object Task{
  implicit val taskRead = Json.reads[Task]
  implicit val taskWrite = Json.writes[Task]
}

