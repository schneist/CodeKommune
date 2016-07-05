package domain

import com.mohiva.play.silhouette.api.{Authenticator, LoginInfo}

/**
  * Created by schneist on 22.06.16.
  */
class Inquisition extends Authenticator{

  override type Value = this.type

  override def loginInfo: LoginInfo = ???

  override def isValid: Boolean = ???

  override type Settings = this.type
}
