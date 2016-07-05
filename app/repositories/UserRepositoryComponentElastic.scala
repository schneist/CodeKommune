package repositories

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticClient, IndexResult, RichSearchResponse}
import domain.{Kommunard, userList, userTree}
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by scsf on 03.03.2016.
  */
trait UserRepositoryComponentElastic  extends UserRepositoryComponent{

  val esClient: ElasticClient

  def userCrud = new  UserCRUDElastic(esClient)


   class UserCRUDElastic (esClient: ElasticClient)  extends  UserCRUD {

    override def addChilduser(parent: userTree, child: userTree): Future[Boolean] = {
      val responseF :Future[IndexResult]  = esClient.execute {
        index into "users/user" fields("name" -> child.name, "parent" -> parent.name)

      }
      responseF.map {
        response => {
          response.created
        }
      }
    }

    override def deleteUser(user:Kommunard): Future[Boolean] = {
      return Future{false}
    }
  }




}
