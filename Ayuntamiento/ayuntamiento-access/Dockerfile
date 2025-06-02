# Imagen base con JDK 17
FROM eclipse-temurin:17-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR generado al contenedor
COPY target/ayuntamiento-access-0.0.1-SNAPSHOT.jar app.jar

# Puerto que expone el servicio
EXPOSE 8094

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]
