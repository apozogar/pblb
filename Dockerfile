# --- Etapa de Construcción (Build Stage) ---
# Usamos una imagen de Maven que ya incluye una versión de Java (OpenJDK 17 en este caso)
# Asegúrate de que la versión de Java coincida con la de tu pom.xml (tienes 21)
# Cambiamos a una imagen que soporte Java 21
FROM maven:3.9-eclipse-temurin-21 AS builder

# Establecemos el directorio de trabajo dentro de la imagen
WORKDIR /app

# 1. Copiamos solo el pom.xml para aprovechar la caché de Docker.
#    Las dependencias solo se descargarán de nuevo si el pom.xml cambia.
COPY pom.xml .

# 2. Descargamos las dependencias de Maven.
RUN mvn dependency:go-offline

# 3. Copiamos todo el resto del código fuente (backend y frontend)
COPY . .

# 4. Construimos el proyecto con Maven.
#    Maven ejecutará el frontend-maven-plugin, que construirá el frontend
#    y lo copiará a la carpeta de recursos de Spring.
#    Luego, Maven compilará el código Java y empaquetará todo.
RUN mvn package -DskipTests

# --- Etapa Final (Final Stage) ---
# Usamos una imagen ligera de Java para ejecutar la aplicación, sin las herramientas de build.
FROM openjdk:21-slim

WORKDIR /app

# Copiamos el artefacto construido (.war) desde la etapa anterior
# Tu pom.xml especifica <packaging>war</packaging>
COPY --from=builder /app/target/*.war app.war

# Exponemos el puerto en el que correrá la aplicación (por defecto 8080 para Spring Boot)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.war"]