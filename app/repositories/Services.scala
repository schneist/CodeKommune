package repositories

import com.sksamuel.elastic4s.ElasticClient
import play.api.Configuration

/**
  * Created by scsf on 03.03.2016.
  */




trait Services{
  val client =  ElasticClient.transport( "elasticsearch://localhost:9300")

  object TaskServiceObj {
    val taskServiceComponent = new TaskRepositoryElastic {
      val esClient = client
    }
  }

}
