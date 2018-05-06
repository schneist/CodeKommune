package domain

import play.api.libs.json.Json

/**
  * Created by scsf on 03.03.2016.
  */

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

