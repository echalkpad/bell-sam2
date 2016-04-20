package com.vennetics.scala.test.simulations

import io.gatling.core.Predef._ // 2
import io.gatling.http.Predef._
import scala.concurrent.duration._

class GetEachSubscriberProfileSimulation extends AbstractSubxSimulation {
  val users = scenario("Users")
    .exec(GetSubscriberProfile.query)


  setUp(
    users.inject(constantUsersPerSec(1) during(3 seconds))).protocols(httpConf)
}
