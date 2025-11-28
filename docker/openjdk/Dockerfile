FROM openjdk:21-jdk-alpine

WORKDIR /app

COPY ./target/modulo1.jar /app/myapp.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]