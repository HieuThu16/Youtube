FROM maven:3.8.5-openjdk-11 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV YOUTUBE_API_KEY=${YOUTUBE_API_KEY}
ENV OPENAI_API_KEY=${OPENAI_API_KEY}
ENV PORT=10000

EXPOSE ${PORT}
ENTRYPOINT ["java", "-jar", "-Dserver.port=${PORT}", "app.jar"] 