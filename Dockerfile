FROM openjdk:8-alpine
ARG JAR_FILE=build/libs/news-0.0.1-SNAPSHOT.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]