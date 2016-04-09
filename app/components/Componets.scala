package components

import com.sksamuel.elastic4s.ElasticClient
import controllers.TreeWebSocket
import play.api.ApplicationLoader.Context
import play.api.i18n._
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, Configuration}
import repositories.TaskRepositoryElastic

class Components(context: Context) extends BuiltInComponentsFromContext(context) with I18nComponents {

  lazy val myComponent = new RepositoryComponent(configuration)
  lazy val applicationController = new controllers.Application(myComponent)
  lazy val assets = new controllers.Assets(httpErrorHandler)
  lazy val tws = new TreeWebSocket

  lazy val router = new Routes(httpErrorHandler,applicationController,tws,assets)
}



class RepositoryComponent(configuration: Configuration) {
  val client = ElasticClient.transport(configuration.getString("elasticsearch.connection").get)



  object TaskRepositoryObj  {
    val taskRepository = new TaskRepositoryElastic {
      val esClient = client
    }
  }
}

class SimpleApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    new Components(context).application
  }
}