## Overview

The Config Server is a [Spring Boot Application](http://projects.spring.io/spring-boot/) which utilises [Spring Cloud Config](http://cloud.spring.io/spring-cloud-config/) server to provide support for externalised configuration in a distributed system. The Spring Cloud Config server is embedded in the Spring Boot Application by annotating the main class with `@EnableConfigServer` and including the spring-cloud-config-server on the classpath.

## Development

Details for development and testing can be [found here](/Vennetics/bell-sam/wiki/Build-and-Test).

## External Configuration

The configuration for `Bell-Sam` is stored on GitHub and is accessible from this location: [Vennetics/bell-sam-config](https://github.com/Vennetics/bell-sam-config). 

### Configuration Files

All configuration files in that repo __must__ be in the [YAML format](https://en.wikipedia.org/wiki/YAML) with the `.yml` file extension for consistency.

The file name for a configuration file **must** match the application name of the client application that will access the configuration file, e.g. for
 the [API Gateway](/api-gateway/src/main/resources/bootstrap.yml) the `spring.application.name` is `api-gateway`

### Spring Profiles

To allow for different configurations to be used in different environments, spring profiles should be used. The Config Server merges properties from the default profile with those of a selected profile when a client queries with a specific profile in the request. The profile names should be consistent across all configuration files, e.g. if we are adding docker specific configuration for certain applications then ensure that the same profile name is used across all files, or another example would be environmental like `staging`, `production`, etc

## Config Clients

The Spring Cloud Config client is embedded in Spring Boot Applications by including the spring-cloud-config-client on the classpath. for more information on clients

[check out this internal page](/Vennetics/bell-sam/wiki/Refreshing-config-without-restart)
