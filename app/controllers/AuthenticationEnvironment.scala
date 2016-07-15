package controllers
import com.mohiva.play.silhouette.api.services.{AuthenticatorService, IdentityService}
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.{SessionAuthenticator, SessionAuthenticatorService}
import com.mohiva.play.silhouette.impl.providers.BasicAuthProvider
import components.{Components, RepositoryComponent}
import domain.Kommunard

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by schneist on 21.06.16.
  */
class AuthenticationEnvironment(implicit val executionContext: ExecutionContext,implicit val components : Components)  extends Environment[KommunardEnv] {


  override def identityService: IdentityService[Kommunard] = new KommunardEntityService(components.repositoryComponent)

  override def eventBus: EventBus = EventBus()

  override def requestProviders: Seq[RequestProvider] = null

  override def authenticatorService: AuthenticatorService[SessionAuthenticator] = ???
}

class KommunardEnv extends Env {
  type I = Kommunard
  type A = SessionAuthenticator
}

class KommunardEntityService(u:RepositoryComponent) extends IdentityService[Kommunard]{
  implicit override def retrieve(loginInfo: LoginInfo): Future[Option[Kommunard]] = {
    return u.UserRepositoryObj.userRepository.userCrud.getUser(loginInfo.providerKey)
  }
}

