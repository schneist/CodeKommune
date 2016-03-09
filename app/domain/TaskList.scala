package domain


import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

/**
  * Created by scsf on 09.03.2016.
  */
case class TaskList(total:Int,tasks : Seq[Task])


object TaskList {
  implicit val taskListReads: Reads[TaskList] = (
    (JsPath \ "total").read[Int] and
      (JsPath \ "hits").read[Seq[Task]]
    ) (TaskList.apply _)
}