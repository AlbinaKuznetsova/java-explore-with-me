FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar appStatServer.jar
ENTRYPOINT ["java","-jar","/appStatServer.jar"]