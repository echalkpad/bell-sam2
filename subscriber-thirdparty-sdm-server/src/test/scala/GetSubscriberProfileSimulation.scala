package com.vennetics.scala.test.simulations

import io.gatling.core.Predef._ // 2
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Single shot getSubscriber profile test randomly selected from CSV file.
 */
class GetSubscriberProfileSimulation extends AbstractSubxSimulation {

  val users = scenario("Users")
    .exec(GetSubscriberProfile.query)

  setUp(
    users.inject(atOnceUsers(1))).protocols(httpConf)
}
