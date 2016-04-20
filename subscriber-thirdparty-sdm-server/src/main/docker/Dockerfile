FROM azul/zulu-openjdk-centos:latest
VOLUME /tmp
ADD bell-sam-subscriber-thirdparty-sdm-server-0.0.3.0.jar app.jar
COPY log4j2.xml /opt/vennetics/sam/config/
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dlogging.config=file:/opt/vennetics/sam/config/log4j2.xml","-Dlogging.file=subscriber-thirdparty-sdm-server-${prefix:hostName}.log","-Dlogging.httpaccess.file=subscriber-thirdparty-sdm-server-httpaccess-${prefix:hostName}.log","-jar","/app.jar"]