FROM java:8
VOLUME /tmp
ADD bell-sam-example-cassandra-spring-data-rest-0.0.7-SNAPSHOT.jar app.jar
COPY log4j2.xml /opt/vennetics/sam/config/
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dlogging.config=file:/opt/vennetics/sam/config/log4j2.xml","-Dlogging.file=example-cassandra-spring-data-rest-${prefix:hostName}.log","-Dlogging.httpaccess.file=example-cassandra-spring-data-rest-httpaccess-${prefix:hostName}.log", "-jar","/app.jar"]
