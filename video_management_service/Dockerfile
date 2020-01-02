FROM maven:3-jdk-8 as builder
WORKDIR /app
COPY . .
RUN mvn package -P development

FROM java:8
WORKDIR /app
COPY --from=builder /app/target/video-management-service-0.0.1-SNAPSHOT.jar ./video-management-service.jar
CMD java -jar video-management-service.jar
