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

# curl lo usa el healthCheck de la task definition de ECS (la imagen JRE no lo trae)
RUN apt-get update \
  && apt-get install -y --no-install-recommends curl \
  && rm -rf /var/lib/apt/lists/*


COPY --from=build /app/target/*.jar /app/app.jar



EXPOSE 6969

# Nota: variable estándar suele ser JAVA_OPTS (sin guion)
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]