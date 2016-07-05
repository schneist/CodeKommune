package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.analyzers.KeywordAnalyzer
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import com.sksamuel.elastic4s.testkit.{ElasticSugar, SearchMatchers}
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import repositories.TaskRepositoryComponentElastic

import scala.concurrent.ExecutionContext.Implicits.global

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
        val taskServiceComponent = new  TaskRepositoryComponentElastic {
          val esClient = client
        }
      }
      TaskServiceObj.taskServiceComponent.childTaskFinder.getChildren("hank").map( t => t.size shouldBe(2))
      TaskServiceObj.taskServiceComponent.childTaskFinder.getChildren("root").map( t => t.size shouldBe(3))
    }

  }
 


}
