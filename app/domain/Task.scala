package domain

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

/**
  * Created by scsf on 03.03.2016.
  */
case class Task(
                 id: String,
                 parent: String,
                 name: String,
                 creationuser: Option[String],
                 assigneduser : Option[String]
               )
object Task {
  implicit val taskReads: Reads[Task] = (
    (JsPath \ "_id").read[String] and
      (JsPath \ "_source" \ "parent").read[String] and
      (JsPath \ "_source" \ "name").read[String] and
        (JsPath \ "_source" \ "creationuser").readNullable[String] and
      (JsPath \ "_source" \ "assigneduser").readNullable[String]
    ) (Task.apply _)
}