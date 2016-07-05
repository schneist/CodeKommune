package components

import com.mohiva.play.silhouette.api.actions.{SecuredAction, UnsecuredAction, UserAwareAction}
import com.mohiva.play.silhouette.api.{Environment, Silhouette, SilhouetteProvider}
import controllers.{AuthenticationEnvironment, KommunardEnv}
import com.sksamuel.elastic4s.ElasticClient
import play.api.ApplicationLoader.Context
import play.api.i18n._
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, Configuration}
import repositories.{TaskRepositoryComponentElastic, UserRepositoryComponentElastic}
import router.Routes

import scala.concurrent.ExecutionContext



class Components(context: Context) extends BuiltInComponentsFromContext(context) with I18nComponents {

val repositoryComponent = new RepositoryComponent(configuration )

  implicit val ec:ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  lazy val authEnv: AuthenticationEnvironment = new AuthenticationEnvironment


  val sil: Silhouette[KommunardEnv] =  new Silhouette[KommunardEnv] {
    override val unsecuredAction: UnsecuredAction = null
    override val userAwareAction: UserAwareAction = null
    override val securedAction: SecuredAction = null
    override val env: Environment[KommunardEnv] = authEnv
  }

  lazy val applicationController = new controllers.Application(this,sil)
  lazy val assets = new controllers.Assets(httpErrorHandler)
  lazy val router = new Routes(httpErrorHandler, applicationController, assets)

}


class RepositoryComponent(configuration: Configuration) {

  val client = ElasticClient.transport(configuration.getString("elasticsearch.connection").get)

  object TaskRepositoryObj  {
    val taskRepository = new TaskRepositoryComponentElastic {
      override val esClient: ElasticClient = client
    }
  }


  object UserRepositoryObj  {
    val userRepository = new UserRepositoryComponentElastic{
      override val esClient: ElasticClient = client
    }
  }
}

class SimpleApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    new Components(context).application
  }
}


