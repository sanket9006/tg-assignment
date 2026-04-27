# Stage 1: Build the Java application using Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the pom.xml and download dependencies (caches this layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final lightweight runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the compiled JAR file from the build stage
COPY --from=build /app/target/tg-assignment-v2-1.0-SNAPSHOT.jar app.jar

# Copy the Northwind SQLite database so the planner has tables to analyze
COPY northwind.db /app/northwind.db

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
