# =========================
# Stage 1: Build (Complile and package the application)
# =========================
FROM eclipse-temurin:25-jdk AS build

# Install Maven manually
RUN apt-get update && apt-get install -y maven

# Now the target folder is at /app/target
WORKDIR /app

# Copy everything in this repo to the image
# COPY <source> <destination>
COPY . .

# Build and package this project as a jar file (run tests)
RUN mvn clean package -DskipTests

# =========================
# Stage 2: Runtime
# =========================
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# COPY <source> <destination>
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]