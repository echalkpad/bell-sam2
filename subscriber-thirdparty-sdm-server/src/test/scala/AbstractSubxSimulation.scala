package com.vennetics.scala.test.simulations

import io.gatling.core.Predef._ // 2
import io.gatling.http.Predef._
import scala.concurrent.duration._

abstract class AbstractSubxSimulation extends Simulation {

  val adminUsername = System.getProperty("adminUsername", "admin")
  val adminPassword = System.getProperty("adminPassword", "admin12345$")
  val host = System.getProperty("host", "192.168.99.100")
  val port = System.getProperty("port", "8087")

  val httpConf = http // 4
    .baseURL("http://" + host + ":" + port)
    .acceptHeader("text/xml;charset=UTF-8")
    .basicAuth("admin", "admin12345$")
    .header("SOAPAction", "")
    
}
