FROM azul/zulu-openjdk-centos:latest
VOLUME /tmp
ADD bell-sam-netapi-sms-enabler-0.0.5-SNAPSHOT.jar app.jar
COPY log4j2.xml /opt/vennetics/sam/config/
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dlogging.config=file:/opt/vennetics/sam/config/log4j2.xml","-Dlogging.file=netapi-sms-enabler-${prefix:hostName}.log","-Dlogging.httpaccess.file=netapi-sms-enabler-httpaccess-${prefix:hostName}.log","-jar","/app.jar"]
