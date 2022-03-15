FROM adoptopenjdk/openjdk11:jdk-11.0.11_9-alpine as BUILDER
VOLUME /tmp
EXPOSE 8081
ADD target/web-apps-platform-0.0.1-SNAPSHOT.jar web-app.jar
ENTRYPOINT ["java", "-jar","web-app.jar"]
