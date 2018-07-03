package application

import com.sksamuel.elastic4s.http.HttpClient
import controllers.EntriesController
import play.api.ApplicationLoader.Context
import play.api.mvc._
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, Configuration}
import play.filters.HttpFiltersComponents
import repo.InMemoryTaskRepo
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
    with ControllerComponents
    with controllers.AssetsComponents {

  val repositoryComponent = new RepositoryComponent(configuration )(executionContext)

  implicit val components: Components = this

  implicit val tk = repositoryComponent.taskRepo

  lazy val ec = new EntriesController()

  lazy val router = new Routes(httpErrorHandler,ec ,assets)

  override def actionBuilder: ActionBuilder[Request, AnyContent] = defaultActionBuilder

  override def parsers: PlayBodyParsers = playBodyParsers

  override def httpFilters: Seq[EssentialFilter] = Seq.empty
}


class RepositoryComponent(configuration: Configuration)(implicit val ec:ExecutionContext) {

  val client = HttpClient.apply(configuration.get[String]("elasticsearch.connection"))

  val taskRepo = new InMemoryTaskRepo
}
