FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD simulation-service-docker-0.1.0.jar app-simulation.jar
ADD train.csv train.csv
RUN sh -c 'touch /app-simulation.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app-simulation.jar" ]
