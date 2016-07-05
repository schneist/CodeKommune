package controllers
import com.mohiva.play.silhouette.api.services.{AuthenticatorResult, AuthenticatorService, IdentityService}
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.util.ExtractableRequest
import domain.{Inquisition, Kommunard}
import play.api.mvc.{RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by schneist on 21.06.16.
  */
class AuthenticationEnvironment(implicit ec: ExecutionContext)  extends Environment[KommunardEnv] {

  override implicit val executionContext: ExecutionContext = ec


  override def identityService: IdentityService[Kommunard] = KommunardEntityService

  override def authenticatorService: AuthenticatorService[Inquisition] = new KommunardAuthenticatorService(ec)

  override def eventBus: EventBus = null

  override def requestProviders: Seq[RequestProvider] = null


}


class KommunardEnv extends Env {
  type I = Kommunard
  type A = Inquisition
}


object KommunardEntityService extends IdentityService[Kommunard]{
   implicit override def retrieve(loginInfo: LoginInfo): Future[Option[Kommunard]] = {
    return Future.successful(Option.empty);
  }
}

class KommunardAuthenticatorService(ec: ExecutionContext) extends AuthenticatorService[Inquisition]{



  override def create(loginInfo: LoginInfo)(implicit request: RequestHeader): Future[Inquisition] = {
    return Future.successful( null)
  }

  override implicit val executionContext: ExecutionContext = ec

  override def update(authenticator: Inquisition, result: Result)(implicit request: RequestHeader): Future[AuthenticatorResult] = null

  override def init(authenticator: Inquisition)(implicit request: RequestHeader): Future[Inquisition] = null

  override def discard(authenticator: Inquisition, result: Result)(implicit request: RequestHeader): Future[AuthenticatorResult] = null

  override def touch(authenticator: Inquisition): Either[Inquisition, Inquisition] = null

  override def retrieve[B](implicit request: ExtractableRequest[B]): Future[Option[Inquisition]] = null

  override def embed(value: Inquisition, result: Result)(implicit request: RequestHeader): Future[AuthenticatorResult] = null

  override def embed(value: Inquisition, request: RequestHeader): RequestHeader = null

  override def renew(authenticator: Inquisition)(implicit request: RequestHeader): Future[Inquisition] = null

  override def renew(authenticator: Inquisition, result: Result)(implicit request: RequestHeader): Future[AuthenticatorResult] = null


}

