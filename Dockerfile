FROM openjdk:17-jdk-slim

COPY *.jar /app.jar

ENV SPRING_PROFILES_ACTIVE=prod

CMD ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${@}"]