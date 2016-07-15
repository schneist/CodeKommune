package domain

import com.mohiva.play.silhouette.api.Identity
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

/**
  * Created by schneist on 22.06.16.
  */
case class Kommunard(
                      id : String,
                      name : String,
                      login : String
                    ) extends Identity

object Kommunard {
  implicit val kommunardReads: Reads[Kommunard] = (
      (JsPath \ "_id").read[String] and
      (JsPath \ "_source" \ "name").read[String] and
      (JsPath \ "_source" \ "login").read[String]
    ) (Kommunard.apply _)
}