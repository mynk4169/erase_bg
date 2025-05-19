FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /app

# Install necessary build tools
RUN apk add --no-cache maven

# Copy only the files needed for dependency resolution
COPY pom.xml .
COPY .mvn/ .mvn
COPY mvnw .

# Add execute permissions to mvnw
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install necessary runtime dependencies
RUN apk add --no-cache tzdata

# Copy the built jar
COPY --from=build /app/target/*.jar app.jar

# JVM Configuration for Railway
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=prod"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 