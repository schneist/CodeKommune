package domain

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

/**
  * Created by scsf on 03.03.2016.
  */
case class TaskTree(name:String, children: Seq[TaskTree]){


  def addChildren(at:String,newLeaves: Seq[TaskTree]): TaskTree ={
    at match {
      case `name` => return new TaskTree(name,newLeaves)
      case _ => return new TaskTree(name,children.map(c => c.addChildren(at,newLeaves)))
    }

  }

}
object TaskTree{
  implicit lazy val treeWrites: Writes[TaskTree] = (
    (JsPath\ "name").write[String] and
      (JsPath \ "children").lazyWrite(Writes.seq[TaskTree](treeWrites))
    )(unlift(TaskTree.unapply))
}
