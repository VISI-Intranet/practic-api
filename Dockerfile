FROM openjdk:19

WORKDIR /app

COPY target/scala-2.13/ScalaBaz-assembly-0.1.0-SNAPSHOT.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]