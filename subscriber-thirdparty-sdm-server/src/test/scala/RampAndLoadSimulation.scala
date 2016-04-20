package com.vennetics.scala.test.simulations

import io.gatling.core.Predef._ // 2
import io.gatling.http.Predef._
import scala.concurrent.duration._

class RampAndLoadSimulation extends AbstractSubxSimulation {

  val numUsers = System.getProperty("numUsers", "20")
  val duration = System.getProperty("duration", "15")

  val users = scenario("Users")
    .exec(GetRandomSubscriberProfile.query)

  setUp(
    users.inject(rampUsers(numUsers.toInt) over (5 seconds),
      constantUsersPerSec(numUsers.toInt) during (duration.toDouble seconds))).protocols(httpConf)
}
