package controllers
import com.mohiva.play.silhouette.api.services.{AuthenticatorResult, AuthenticatorService, IdentityService}
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.util.ExtractableRequest
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import domain.Kommunard
import play.api.mvc.{RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by schneist on 21.06.16.
  */
class AuthenticationEnvironment(implicit ec: ExecutionContext)  extends Environment[KommunardEnv] {

  override implicit val executionContext: ExecutionContext = ec


  override def identityService: IdentityService[Kommunard] = KommunardEntityService


  override def eventBus: EventBus = null

  override def requestProviders: Seq[RequestProvider] = null

  override def authenticatorService: AuthenticatorService[SessionAuthenticator] = null
}


class KommunardEnv extends Env {
  type I = Kommunard
  type A = SessionAuthenticator
}


object KommunardEntityService extends IdentityService[Kommunard]{
  implicit override def retrieve(loginInfo: LoginInfo): Future[Option[Kommunard]] = {
    return Future.successful(Option.empty);
  }
}

