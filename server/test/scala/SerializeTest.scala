package scala

import main.scala.shared.model.{GetMessage, WebSocketMessage}
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.Json

/**
  * Created by stefan.schneider on 13.10.2016.
  */
class SerializeTest extends WordSpec with Matchers {

  val cM = GetMessage(id="1")

  val message = WebSocketMessage(
    Message = cM,
    requestID = "1"
  )

  "CommandMessageJSON" should {
    "write and read a KafkaMessage" in {
      val asJson = Json.toJson(message)
      val messageR = asJson.validate[WebSocketMessage].get
      message shouldBe messageR
    }
  }
}