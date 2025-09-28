# --- Etapa de Construcción (Build Stage) ---
# Usamos una imagen de Maven con Java 21 para construir el proyecto.
FROM maven:3.9-eclipse-temurin-21 AS build

# Establecemos el directorio de trabajo.
WORKDIR /app

# Copiamos solo el pom.xml para cachear las dependencias de Maven.
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el resto del código fuente del backend.
COPY src ./src

# Construimos el proyecto y generamos el archivo .jar.
RUN mvn package -DskipTests

# --- Etapa Final (Final Stage) ---
# Usamos una imagen ligera de Java para ejecutar la aplicación.
FROM openjdk:21-slim
WORKDIR /app

# Copiamos el artefacto construido (.jar) desde la etapa anterior.
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto 8080.
EXPOSE 8080

# Comando para ejecutar la aplicación.
ENTRYPOINT ["java", "-jar", "app.jar"]