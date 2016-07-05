package repositories

import domain._

import scala.concurrent.Future

/**
  * Created by scsf on 03.03.2016.
  */
trait UserRepositoryComponent {

  

  def userCrud:UserCRUD

  trait UserCRUD {
    def addUser(user:Kommunard) : Future[Boolean]
    def deleteUser(user:Kommunard) : Future[Boolean]
    def getUser(login:String) : Future[Kommunard]
  }


}
