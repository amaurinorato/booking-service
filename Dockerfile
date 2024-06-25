FROM azul/zulu-openjdk-alpine:17.0.2-jre

ARG PROJECT_JAR_PATH

COPY ${PROJECT_JAR_PATH} booking-service.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /booking-service.jar"]