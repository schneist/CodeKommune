import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}

import scala.concurrent.Future


import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by scsf on 12.02.2016.
  */
@RunWith(classOf[JUnitRunner])
class JsonTest extends Specification {


  "Application" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
    }

    "render the index page" in new WithApplication{
      val treedata : Future[Result]  = route(FakeRequest(GET, "/treedata.json")).get
      status(treedata) must equalTo(OK)
    }
  }

}
