## Overview

The Discovery Service is a [Spring Boot Application](http://projects.spring.io/spring-boot/) which utilises [Spring Cloud Netflix](http://cloud.spring.io/spring-cloud-netflix/) Eureka Server to provide support for Service to be registered as well as allowing clients to discover the registered instances using Spring-managed beans. The Spring Cloud Netflix Eureka Server is embedded in the Spring Boot Application by annotating the main class with `@EnableEurekaServer` and including the `spring-cloud-starter-eureka` dependency on the classpath.

## Development

Details for development and testing can be [found here](/Vennetics/bell-sam/wiki/Build-and-Test).

## Standalone Mode

The Eureka Server is configured to run in `Standalone Mode`. This is a robust solution for our current needs as both the client and server eureka instance have their own in-memory caches of eureka registration. The clients in-memory cache means that they do not need to go to the registry for every single request to another service). In Standalone mode, the Eurkea Server is not aware of any peers that may exist in other clusters/regions. If this awareness is needed in future then start with the [Peer Awareness](http://projects.spring.io/spring-cloud/docs/1.0.1/spring-cloud.html#_peer_awareness)

## Eureka Clients

The Spring Cloud Netflix Eureka client is embedded in Spring Boot Applications by including the `spring-cloud-starter-eureka` on the classpath and `@EnableEurekaClient`. This makes the application both an Eureka "instance" (i.e. it registers itself) and a "client" (i.e. it can query the registry to locate other services).
