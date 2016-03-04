package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.analyzers.KeywordAnalyzer
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import com.sksamuel.elastic4s.testkit.{SearchMatchers, ElasticSugar}
import com.sun.jmx.snmp.tasks.TaskServer
import org.scalatest.{FreeSpec, Matchers}
import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.WordSpec
import org.scalatest.mock.MockitoSugar
import org.specs2.control.Ok
import play.api.test.FakeRequest

import play.api.test._
import play.api.test.Helpers._
import repositories.TaskRepositoryElastic

import services.TaskServiceElasticComponent


class JsonSpec extends WordSpec with Matchers with SearchMatchers with ElasticSugar with MockitoSugar {

  client.execute {
    create index "tasks" mappings {
      mapping("task") fields (
        "name" typed StringType analyzer KeywordAnalyzer ,
        "parent" typed StringType analyzer KeywordAnalyzer
        )
    }
  }.await

  client.execute(
    bulk(
      index into "tasks/task" fields("name" -> "root", "parent" -> "root"),
      index into "tasks/task" fields("name" -> "hank", "parent" -> "root"),
      index into "tasks/task" fields("name" -> "jesse", "parent" -> "hank"),
      index into "tasks/task" fields("name" -> "gus", "parent" -> "hank")
    )
  ).await

  refresh("tasks")
  blockUntilCount(4, "tasks")


  "treedata" should {

    "contain all" in {
      (search in "tasks" query "*") should haveHits(4)
    }
  }

  "task repo " should {
    "find all children" in {
      object TaskServiceObj {
        val taskServiceComponent = new TaskServiceElasticComponent with TaskRepositoryElastic {
          val esClient = client
        }
      }
      TaskServiceObj.taskServiceComponent.childTaskFinder.getChildren("hank").size shouldBe(2)
      System.out.println(TaskServiceObj.taskServiceComponent.childTaskFinder.getChildren("hank"))

    }

  }
  //"findall" should{
  //"find all " in new WithApplication{
  //val treedata = route(FakeRequest(GET, "/treedata.json")).get
  //status(treedata)  shouldBe  Ok
  //contentType(treedata) must beSome.which(_ == "text/html")
  //contentAsString(treedata) must contain ("Your new application is ready.")
  // }

  //}



}