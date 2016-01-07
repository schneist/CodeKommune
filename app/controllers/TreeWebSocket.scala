package controllers

import play.api._
import play.api.mvc._
import com.sksamuel.elastic4s.{RichGetResponse, ElasticClient}
import com.sksamuel.elastic4s.ElasticDsl._
import scala.concurrent.Future
import play.api.libs.iteratee.Iteratee
import play.api.libs.json.{JsNull,Json,JsString,JsValue,JsResult}
import play.api.libs.json.Json
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsError
import play.api.libs.iteratee.Concurrent
import play.api.libs.iteratee.Concurrent.Channel
import scala.concurrent.ExecutionContext.Implicits.global


class TreeWebSocket extends Controller {

	val client = ElasticClient.transport("elasticsearch://localhost:9300")


  def getTasks( client: ElasticClient, channel:Channel[JsValue]) : Future[Unit] = scala.concurrent.Future {
    val response : Future[RichGetResponse] = client.execute {
      get id "root" from "ck"
    }

    val responseString = response.map { f =>  f.field("name").getValue }

    System.out.println(responseString.toString)
  }

	def indexNewTask(res: JsValue , client: ElasticClient, channel:Channel[JsValue]) : Future[Unit] = scala.concurrent.Future {
    val ert = client.execute { index into "tasks"   }
    val FResponse : Future[JsValue] = ert.map { x => Json.parse("{ \"id\" : \"" + x.getId + "\"}") }
    FResponse.map { x =>    channel.push(x) }
	}

  val (out,channel) = Concurrent.broadcast[JsValue]
	def connect = WebSocket.using[JsValue] { request =>
    val in: Iteratee[JsValue, Unit] = Iteratee.foreach[JsValue] { msg =>
    (msg \ "action").validate[String] match {
      case e: JsError => Logger.error("Unable to parse action" + JsError.toFlatJson(e).toString())
      case s: JsSuccess[String] => {
    	  s.value match {
  				  case "addTask" => {
      				 indexNewTask(msg.\("task").get,client,channel)
  					}
            case "getTasks" => {
              getTasks(client,channel)
            }
  				}
  			}
      }
    }
	  (in,out)
 }
}