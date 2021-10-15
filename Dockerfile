FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 8081
ADD target/web-apps-platform-0.0.1-SNAPSHOT.jar web-app.jar
ENTRYPOINT ["java", "-jar","web-app.jar"]
