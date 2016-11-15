package domain

import play.api.libs.json.Json

/**
  * Created by scsf on 03.03.2016.
  */

case class Task(
                 id: Option[String],
                 parent: String,
                 name: String,
                 creationUser: Option[String],
                 assignedUser : Option[String]
               )

object Task{
  implicit val taskRead = Json.reads[Task]
  implicit val taskWrite = Json.writes[Task]
}