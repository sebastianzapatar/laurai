# ============
# Build stage
# ============
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app


COPY mvnw pom.xml ./
COPY .mvn/ .mvn/

# Permisos y descarga de dependencias (sin tests)
RUN chmod +x ./mvnw \
  && ./mvnw -DskipTests dependency:go-offline

# Ahora sí copia el código fuente
COPY src/ src/

# Compila y empaqueta
RUN ./mvnw -DskipTests clean package

# ============
# Runtime stage
# ============
FROM eclipse-temurin:25-jre
WORKDIR /app


COPY --from=build /app/target/*.jar /app/app.jar



EXPOSE 8080

# Nota: variable estándar suele ser JAVA_OPTS (sin guion)
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]