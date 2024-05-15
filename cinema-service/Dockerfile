FROM openjdk:17 AS builder
ARG JAR_FILE=target/cinema-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} cinema-service.jar
RUN java -Djarmode=layertools -jar cinema-service.jar extract

FROM openjdk:17
VOLUME /tmp
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
