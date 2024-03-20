FROM openjdk:17.0.2-jdk-slim-buster
ARG JAR_FILE=*.jar
ARG DRIVERS=drivers
ENV SQLONLINE=./sqlonline
WORKDIR $SQLONLINE
COPY $JAR_FILE application.jar
COPY $DRIVERS drivers
ENV DRIVERS_DIR=drivers
ENTRYPOINT ["java", "-jar", "application.jar"]