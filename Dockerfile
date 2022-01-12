FROM openjdk:11-jre-slim as builder
EXPOSE  8280
ADD /target/Inetum.jar Inetum.jar
ENTRYPOINT ["java","-jar","/Inetum.jar"]