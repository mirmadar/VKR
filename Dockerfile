FROM openjdk:17

WORKDIR /vkr

COPY build/libs/*.jar /vkr/vkr.jar

ENTRYPOINT ["java", "-jar", "/vkr/vkr.jar"]