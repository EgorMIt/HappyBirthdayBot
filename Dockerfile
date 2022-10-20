#define base docker image
FROM openjdk:17
LABEL maintainer="Egor Mitrofanov"
ADD target/HappyBirthdayBot-0.0.1-SNAPSHOT.jar HappyBirthdayBot.jar
ENTRYPOINT ["java", "-jar", "HappyBirthdayBot.jar"]