FROM azul/zulu-openjdk-centos:latest
VOLUME /tmp
ADD bell-sam-logaggregator-0.0.7-SNAPSHOT.jar app.jar
COPY log4j2.xml /opt/vennetics/sam/config/
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dlogging.config=file:/opt/vennetics/sam/config/log4j2.xml", "-Dlogging.file=loggeraggregator-${prefix:hostName}.log","-Dlogging.httpaccess.file=loggeraggregator-httpaccess-${prefix:hostName}.log","-jar","/app.jar"]
