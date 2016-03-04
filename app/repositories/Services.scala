package repositories

import services.TaskServiceComponent

/**
  * Created by scsf on 03.03.2016.
  */
object Services {

  val taskSeriveComponent = new TaskServiceComponent with TaskRepositoryElastic{


  }

}
