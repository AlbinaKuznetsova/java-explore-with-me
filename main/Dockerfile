FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar appMain.jar
ENTRYPOINT ["java","-jar","/appMain.jar"]