package repositories

import com.sksamuel.elastic4s.ElasticClient
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
      return Future{false}
    }

    override def addUser(user: Kommunard): Future[Boolean] = {
      return Future{false}
    }

    override def getUser(login: String): Future[Kommunard] = {
      return Future{ new Kommunard()}
    }
  }
}
