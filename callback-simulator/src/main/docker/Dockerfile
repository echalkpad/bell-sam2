FROM azul/zulu-openjdk-centos:latest
VOLUME /tmp
ADD bell-sam-callback-simulator-0.0.4.0.jar app.jar
COPY log4j2.xml /opt/vennetics/sam/config/
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Dlogging.config=file:/opt/vennetics/sam/config/log4j2.xml","-Dlogging.file=callback-simulator-${prefix:hostName}.log","-Dlogging.httpaccess.file=callback-simulator-httpaccess-${prefix:hostName}.log","/app.jar"]
