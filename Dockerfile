# Use Maven + JDK 17 for building
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy everything and build the jar
COPY . .

# Build the jar and skip tests
RUN mvn clean package -DskipTests

# Use a smaller JRE image for running the app
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port (optional, Render sets $PORT dynamically)
EXPOSE 8080

# Run the jar using the Render-provided PORT
CMD ["sh", "-c", "java -Dserver.port=$PORT -jar /app/app.jar"]
