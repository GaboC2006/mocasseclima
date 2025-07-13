# MocasseClima

Microservicio REST para información meteorológica en tiempo real utilizando la API de OpenWeather.

## Descripción
Este proyecto es un microservicio Spring Boot que proporciona:
- Consulta de clima actual por ciudad y país
- Historial de consultas almacenadas en base de datos
- Documentación automática con OpenAPI/Swagger
- Implementación de HATEOAS para navegabilidad

## Características Principales
- Consulta de datos meteorológicos en tiempo real
- Almacenamiento de historial de consultas
- API REST bien documentada
- Pruebas unitarias y de integración
- Manejo de errores personalizado

## Tecnologías Utilizadas
- Spring Boot 3.4.5
- Spring Data JPA
- H2 Database (para desarrollo)
- Lombok
- SpringDoc OpenAPI 2.3.0
- JUnit 5
- Mockito

## Endpoints Disponibles
### Consulta del Clima
- `GET /api/clima/{ciudad}?pais={codigoPais}`
  - Ejemplo: `/api/clima/Madrid?pais=ES`
  - Parámetros:
    - `ciudad`: Nombre de la ciudad (requerido)
    - `pais`: Código ISO 3166-1 alpha-2 del país (requerido)

### Historial de Consultas
- `GET /api/clima/historial/{ciudad}?pais={codigoPais}`
- `GET /api/clima/historial/fechas?fechaInicio=dd-MM-yyyy&fechaFin=dd-MM-yyyy`

## Documentación API
- Swagger UI: `/swagger-ui.html`
- OpenAPI docs: `/v3/api-docs`

## Ejecución
1. Configurar clave API en `application.properties`:
   ```properties
   openweather.api.key=TU_CLAVE_API
   openweather.api.url=https://api.openweathermap.org/data/2.5/weather
   ```
## Manejo de Errores
El servicio incluye manejo de errores para:

- Ciudad no encontrada (404)
- Errores del servidor (500)
- Validación de parámetros
