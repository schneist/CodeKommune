package repositories

import com.sksamuel.elastic4s.ElasticClient
import services.{TaskServiceElasticComponent, TaskServiceComponent}

/**
  * Created by scsf on 03.03.2016.
  */
object Services {

  val client = ElasticClient.transport("elasticsearch://localhost:9300")

  object TaskServiceObj {
    val taskServiceComponent = new TaskServiceElasticComponent with TaskRepositoryElastic {
      val esClient = client
    }
  }

}
