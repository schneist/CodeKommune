package controllers

import akka.actor.ActorSystem
import cats.implicits._
import gql.TaskQueries
import play.api.libs.json.{JsObject, JsString, Json}
import play.api.mvc.{Action, _}
import repo.TaskRepository
import sangria.ast.Document
import sangria.execution._
import sangria.marshalling.playJson._
import sangria.parser.{QueryParser, SyntaxError}
import sangria.renderer.SchemaRenderer

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
class EntriesController (implicit exec: ExecutionContext,
                         cc: ControllerComponents,
                         taskRepo:TaskRepository
                        )extends AbstractController(cc){

  def index = Action {
    Ok(views.html.index(""))
  }
  def renderSchema = Action {
    Ok(SchemaRenderer.renderSchema(TaskQueries.schema))
  }


  def graphql = Action.async(parse.json) { request ⇒
    val query = (request.body \ "query").as[String]
    val operation = (request.body \ "operationName").asOpt[String]
    val variables = (request.body \ "variables").toOption.map {
      case JsString(vars) ⇒ if (vars.trim == "" || vars.trim == "null") Json.obj() else Json.parse(vars).as[JsObject]
      case obj: JsObject ⇒ obj
      case _ ⇒ Json.obj()
    }



    QueryParser.parse(query) match {
      // query parsed successfully, time to execute it!
      case Success(queryAst) ⇒
        executeGraphQLQuery(queryAst, operation, variables.getOrElse(Json.obj()))

      // can't parse GraphQL query, return error
      case Failure(error: SyntaxError) ⇒
        Future.successful(BadRequest(Json.obj("error" → error.getMessage)))
      case Failure(t: Throwable) ⇒
        Future.successful(BadRequest(Json.obj("error" → t.getMessage)))

    }
  }

  def executeGraphQLQuery(query: Document, op: Option[String], vars: JsObject) =
    Executor.execute(TaskQueries.schema, query,taskRepo, operationName = op, variables = vars)
      .map(Ok(_))
      .recover {
        case error: QueryAnalysisError ⇒ BadRequest(error.resolveError)
        case error: ErrorWithResolver ⇒ InternalServerError(error.resolveError)
      }

}
