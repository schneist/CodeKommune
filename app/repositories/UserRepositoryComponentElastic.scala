package repositories

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticClient}
import domain.Kommunard

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by scsf on 03.03.2016.
  */
trait UserRepositoryComponentElastic  extends UserRepositoryComponent{

  val esClient: ElasticClient

  def userCrud = new UserCRUDElastic(esClient)

  class UserCRUDElastic (esClient: ElasticClient)  extends  UserCRUD {

    override def deleteUser(user:Kommunard): Future[Boolean] = {
      Future{false}
    }

    override def addUser(user: Kommunard): Future[Boolean] = {
      esClient.execute {
        index into "users/user" fields(
          "name" -> user.name
          )
      }
      .map {
        response => {
          response.created
        }
      }
    }

    override def getUser(login: String): Future[Option[Kommunard]] = {
      Future{ Option.apply(new Kommunard("","",login))}
    }
  }
}
