FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY /build/libs/demo-0.0.1.jar .
CMD ["java", "-jar", "demo-0.0.1.jar"]