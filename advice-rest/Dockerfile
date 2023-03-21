FROM adoptopenjdk/openjdk11:debian
RUN mkdir /opt/app
COPY /build/libs/advice-rest-0.1.jar /opt/app
CMD ["java", "-jar", "/opt/app/advice-rest-0.1.jar"]