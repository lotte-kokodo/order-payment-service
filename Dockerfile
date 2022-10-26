FROM openjdk:11-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY build/libs/order-payment-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
