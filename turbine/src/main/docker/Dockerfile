FROM azul/zulu-openjdk-centos:latest
VOLUME /tmp
ADD bell-sam-turbine-0.0.7-SNAPSHOT.jar app.jar
COPY log4j2.xml /opt/vennetics/sam/config/
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dlogging.config=file:/opt/vennetics/sam/config/log4j2.xml","-Dlogging.file=turbine-${prefix:hostName}.log","-Dlogging.httpaccess.file=turbine-httpaccess-${prefix:hostName}.log","-jar","/app.jar"]
