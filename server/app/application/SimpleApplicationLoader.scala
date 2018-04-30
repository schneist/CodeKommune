package application

import com.sksamuel.elastic4s.ElasticClient
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, Configuration}
import play.api.ApplicationLoader.Context
import play.api.i18n.I18nComponents
import play.filters.HttpFiltersComponents
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

class Components(context: Context)
  extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with controllers.AssetsComponents {

  val repositoryComponent = new RepositoryComponent(configuration )

  implicit val components: Components = this




  lazy val router = new Routes(httpErrorHandler)

}


class RepositoryComponent(configuration: Configuration) {

  val client = ElasticClient.transport(configuration.getString("elasticsearch.connection").get)

}
