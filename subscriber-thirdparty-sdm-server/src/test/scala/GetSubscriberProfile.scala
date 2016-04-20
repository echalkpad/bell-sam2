package com.vennetics.scala.test.simulations

import io.gatling.core.Predef._ // 2
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Executes a getSubscriberProfile request fed from the test-queries CSV
 */
object GetSubscriberProfile {

  val testQueries = csv("./request-data/test-queries.csv").circular

  val query = exec()
    .feed(testQueries).exec(
      http("GetSubscriberProfile")
        .post("/bell/subscriber/subscriber-thirdparty-sdm-server/subscriber/v1_0")
        .body(ELFileBody("./request-bodies/GetSubscriberProfileRequest.xml"))
        //.check(status.is(Integer.getInteger("${status}", 200).toInt))
        //.check(headerRegex("status", "${status}").ofType[String])
        //.check(regex("<key>${filter}</key>"))
        //.check(regex("<value>${value}</value>"))
        )
}
