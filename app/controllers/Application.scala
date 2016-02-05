package controllers

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{RichGetResponse, ElasticClient}
import play.api._
import play.api.mvc._

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global


class Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  def tree = Action {
    Ok(views.html.tree())
  }

  def treedata = Action {

    val client = ElasticClient.transport("elasticsearch://localhost:9300")

    val response : Future[RichGetResponse] =	client.execute { get("root").from("ck")	}
    response.foreach(f => f.field("name"))

    val responseString = response.map { f => f.sourceAsString }
    Ok(responseString.await)

  }
}
