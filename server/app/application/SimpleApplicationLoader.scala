package application

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.http.HttpClient
import controllers.EntriesController
import domain.Task
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, Configuration}
import play.api.ApplicationLoader.Context
import play.api.i18n.I18nComponents
import play.api.mvc._
import play.filters.HttpFiltersComponents
import repo.TaskRepository
import router.Routes

import scala.concurrent.{ExecutionContext, Future}

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

  val repositoryComponent = new RepositoryComponent(configuration )

  implicit val components: Components = this

  implicit  val tk = repositoryComponent.taskRepo
  lazy val ec = new EntriesController()

  lazy val router = new Routes(httpErrorHandler,ec ,assets)

  override def actionBuilder: ActionBuilder[Request, AnyContent] = defaultActionBuilder

  override def parsers: PlayBodyParsers = playBodyParsers

  override def httpFilters: Seq[EssentialFilter] = Seq.empty
}


class RepositoryComponent(configuration: Configuration) {

  val client = HttpClient.apply(configuration.get[String]("elasticsearch.connection"))

  val taskRepo = new TaskRepository
  {
    override def searchTask(query: String): Future[List[Task]] = ???

    override def deleteTask(id: String): Future[Boolean] = ???

    override def addTask(task: Task): Future[Task] = ???

    override def getTask(id: String): Future[Task] = ???
  }
}
