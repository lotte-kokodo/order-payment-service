FROM openjdk:11-jre-slim-buster
ARG JAR_FILE=target/*.jar
COPY build/libs/order-payment-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
VOLUME ["/var/log"]
ENTRYPOINT ["java","-jar","/app.jar"]