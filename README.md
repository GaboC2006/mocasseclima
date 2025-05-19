# MocasseClima

Microservicio REST para información meteorológica en tiempo real utilizando la API de OpenWeather.

## Requisitos

- Java 17
- Maven
- Clave API de OpenWeather

## Configuración

1. Añade tu clave API:
   ```properties
   openweather.api.key=TU_CLAVE_API

## Ejecución
   mvn spring-boot:run

## Endpoints
- GET /api/clima/{ciudad}?pais={codigoPais} Ejemplo: /api/clima/Santiago?pais=CL

## Documentación API
   - Swagger UI: /swagger-ui.html
   - OpenAPI docs: /api-docs
