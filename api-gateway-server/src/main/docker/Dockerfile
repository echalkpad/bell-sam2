FROM azul/zulu-openjdk-centos:latest
VOLUME /tmp
ADD bell-sam-api-gateway-server-0.0.6-SNAPSHOT.jar app.jar
COPY log4j2.xml /opt/vennetics/sam/config/
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dlogging.config=file:/opt/vennetics/sam/config/log4j2.xml","-Dlogging.file=api-gateway-server-${prefix:hostName}.log","-Dlogging.httpaccess.file=api-gateway-server-httpaccess-${prefix:hostName}.log","-jar","/app.jar"]
