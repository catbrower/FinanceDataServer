FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/financeDataServer-0.0.1-SNAPSHOT.jar /app/financeDataServer.jar

EXPOSE 53045

ENTRYPOINT ["java", "-Dspring.data.mongodb.uri=mongodb://172.17.0.1:27017/polygon", "-jar", "/app/financeDataServer.jar"]