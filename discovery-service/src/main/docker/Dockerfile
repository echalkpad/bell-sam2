FROM java:8
VOLUME /tmp
ADD bell-sam-discovery-service-0.0.6.0.jar app.jar
COPY log4j2.xml /opt/vennetics/sam/config/
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dlogging.config=file:/opt/vennetics/sam/config/log4j2.xml","-Dlogging.file=discovery-service-${prefix:hostName}.log","-Dlogging.httpaccess.file=discovery-service-httpaccess-${prefix:hostName}.log","-jar","/app.jar"]
