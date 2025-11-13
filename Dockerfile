# Build stage - Use JDK 17
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage - Use JDK 17  
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (Render uses 10000)
EXPOSE 10000

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
