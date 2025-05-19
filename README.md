# MocasseClima

Microservicio REST para información meteorológica en tiempo real utilizando la API de OpenWeather.

## Descripción
Este proyecto es un microservicio que proporciona información meteorológica actualizada para cualquier ciudad del mundo. Utiliza la API de OpenWeather para obtener datos precisos y los presenta en un formato fácil de consumir.

## Requisitos Previos
- Java 17
- Maven
- Clave API de OpenWeather

## Configuración
1. Clona el repositorio
2. Crea un archivo `application-dev.properties` en `src/main/resources/`
3. Añade tu clave API:
   ```properties
   openweather.api.key=TU_CLAVE_API

## Instalación y Ejecución
1. Compila el proyecto:
   
   ```
   mvn clean install
   ```
2. Ejecuta la aplicación:
   
   ```
   mvn spring-boot:run
   ```
## Endpoints Disponibles
### Consulta del Clima
- GET /api/clima/{ciudad}?pais={codigoPais}
  - Ejemplo: /api/clima/Santiago?pais=CL
  - Parámetros:
    - ciudad : Nombre de la ciudad
    - pais : Código del país (formato ISO 3166-1 alpha-2)
## Respuesta del API
El servicio devuelve información sobre:

- Temperatura actual
- Sensación térmica
- Temperaturas máxima y mínima
- Humedad
- Presión atmosférica
- Descripción del clima
- Horarios de amanecer y atardecer
## Documentación API
- Swagger UI: /swagger-ui.html
- OpenAPI docs: /api-docs
## Tecnologías Utilizadas
- Spring Boot
- Maven
- Lombok
- SpringDoc OpenAPI
## Manejo de Errores
El servicio incluye manejo de errores para:

- Ciudad no encontrada (404)
- Errores del servidor (500)
- Validación de parámetros
