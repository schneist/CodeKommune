package application

import com.sksamuel.elastic4s.ElasticClient
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, Configuration}
import play.api.ApplicationLoader.Context
import play.api.i18n.I18nComponents
import router.Routes

import scala.concurrent.ExecutionContext

/**
  * Created by stefan.schneider on 31.10.2016.
  */
class SimpleApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    new Components(context).application
  }
}

class Components(context: Context) extends BuiltInComponentsFromContext(context) with I18nComponents {

  val repositoryComponent = new RepositoryComponent(configuration )


  implicit val executionContext :ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  implicit val components: Components = this


  lazy val treeWebSocket = new controllers.TreeWebsocket()
  lazy val assets = new controllers.Assets(httpErrorHandler)
  lazy val router = new Routes(httpErrorHandler,treeWebSocket)

}


class RepositoryComponent(configuration: Configuration) {

  val client = ElasticClient.transport(configuration.getString("elasticsearch.connection").get)

}
