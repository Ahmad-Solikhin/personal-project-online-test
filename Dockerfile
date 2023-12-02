FROM maven:3.8.5-openjdk-17 as build

WORKDIR app

COPY src ./src
COPY pom.xml .

RUN mvn clean -DskipTests package

FROM openjdk:17-jdk-alpine
LABEL authors="ahmadsgr39"

#RUN apk add openjdk17-jdk

WORKDIR app

ARG DB_USERNAME_POSTGRES
ARG DB_PASSWORD
ARG EMAIL_USERNAME
ARG EMAIL_PASSWORD

ENV SIGNATURE_KEY="U6xOqiivo/fsQWEFhGQ1osn28gfjPticu9eW1VNapNY="
ENV ACTIVE_PROFILE="prod"
ENV DB_USERNAME_POSTGRES=${DB_USERNAME_POSTGRES}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV EMAIL_USERNAME=${EMAIL_USERNAME}
ENV EMAIL_PASSWORD=${EMAIL_PASSWORD}
ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV APP_PORT=8080

EXPOSE ${APP_PORT}/tcp

COPY --from=build /app/target/personal-project-online-test.jar spring-boot.jar

#COPY target/${JAR_FILE_NAME}.jar spring-boot.jar

ENTRYPOINT ["java", "-jar", "spring-boot.jar", "--spring.profiles.active=${ACTIVE_PROFILE}"]