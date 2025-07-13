package proyecto.mocasseclima.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import proyecto.mocasseclima.exception.CityNotFoundException;
import proyecto.mocasseclima.exception.WeatherServiceException;
import proyecto.mocasseclima.model.RegistroClima;
import proyecto.mocasseclima.model.WeatherResponse;
import proyecto.mocasseclima.service.WeatherService;
import proyecto.mocasseclima.controller.WeatherController;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherControllerTest {

    @Mock
    private WeatherService weatherServiceMock;

    @InjectMocks
    private WeatherController weatherController;


@Test
void getWeatherByCity_DeberiaRetornarDatosClima_CuandoLaConsultaEsExitosa() {
    // Arrange: Preparar datos de entrada y comportamiento simulado del servicio
    
    String ciudad = "Madrid";
    String pais = "ES";
    
    // Crear una respuesta simulada con datos climáticos para la ciudad
    WeatherResponse mockResponse = new WeatherResponse();
    mockResponse.setNombreCiudad(ciudad);
    
    WeatherResponse.DatosClimaticos datosClimaticos = new WeatherResponse.DatosClimaticos();
    datosClimaticos.setTemperatura(20.5);
    mockResponse.setDatosClimaticos(datosClimaticos);
    
    WeatherResponse.Sistema sistema = new WeatherResponse.Sistema();
    sistema.setPais(pais);
    mockResponse.setSistema(sistema);
    
    // Configurar el mock para que devuelva la respuesta simulada cuando se llame al servicio
    when(weatherServiceMock.obtenerClimaPorCiudad(ciudad, pais)).thenReturn(mockResponse);
    
    // Act: Ejecutar el método que se va a probar
    ResponseEntity<EntityModel<WeatherResponse>> response = weatherController.getWeatherByCity(ciudad, pais);
    
    // Assert: Verificar que la respuesta sea la esperada
    assertEquals(HttpStatus.OK, response.getStatusCode(), "El código HTTP debe ser 200 OK");
    assertNotNull(response.getBody(), "El cuerpo de la respuesta no debe ser nulo");
    assertEquals(ciudad, response.getBody().getContent().getNombreCiudad(), "El nombre de la ciudad debe coincidir");
}


@Test
void getWeatherByCity_DeberiaRetornarNotFound_CuandoLaCiudadNoExiste() {
    // Arrange: Definir ciudad y país inexistentes para la prueba
    String ciudad = "CiudadInexistente";
    String pais = "XX";
    
    // Configurar el mock para que lance una excepción CityNotFoundException
    // cuando se intente obtener el clima de una ciudad que no existe
    when(weatherServiceMock.obtenerClimaPorCiudad(ciudad, pais))
        .thenThrow(new CityNotFoundException("Ciudad no encontrada"));
    
    // Act: Llamar al método del controlador con la ciudad y país definidos
    ResponseEntity<EntityModel<WeatherResponse>> response = weatherController.getWeatherByCity(ciudad, pais);
    
    // Assert: Verificar que la respuesta tenga código HTTP 404 Not Found
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
}

@Test
void getWeatherByCity_DeberiaRetornarInternalServerError_CuandoHayErrorDeServicio() {
    // Arrange: Definir ciudad y país para la prueba
    String ciudad = "Madrid";
    String pais = "ES";
    
    // Configurar el mock para que lance una excepción WeatherServiceException
    // simulando un error interno del servicio al obtener el clima
    when(weatherServiceMock.obtenerClimaPorCiudad(ciudad, pais))
        .thenThrow(new WeatherServiceException("Error de servicio"));
    
    // Act: Llamar al método del controlador con la ciudad y país definidos
    ResponseEntity<EntityModel<WeatherResponse>> response = weatherController.getWeatherByCity(ciudad, pais);
    
    // Assert: Verificar que la respuesta tenga código HTTP 500 Internal Server Error
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
}


@Test
void getWeatherHistory_DeberiaRetornarHistorialDeClima() {
    // Arrange: Definir ciudad y país para la consulta del historial
    String ciudad = "Madrid";
    String pais = "ES";
    
    // Crear una lista simulada de registros de clima para la ciudad y país indicados
    List<RegistroClima> registros = Arrays.asList(
        crearRegistroClima(1L, ciudad, pais, 22.5),
        crearRegistroClima(2L, ciudad, pais, 23.0)
    );
    
    // Configurar el mock para que devuelva la lista simulada cuando se llame al servicio
    when(weatherServiceMock.obtenerHistorialClima(ciudad, pais)).thenReturn(registros);
    
    // Act: Ejecutar el método del controlador que obtiene el historial de clima
    ResponseEntity<?> response = weatherController.getWeatherHistory(ciudad, pais);
    
    // Assert: Verificar que la respuesta tenga código HTTP 200 OK y que el cuerpo no sea nulo
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
}

@Test
void getWeatherHistoryByDates_DeberiaRetornarHistorialPorFechas() {
    // Arrange: Definir rango de fechas para la consulta del historial
    LocalDate fechaInicio = LocalDate.now().minusDays(7);
    LocalDate fechaFin = LocalDate.now();
    
    // Crear una lista simulada de registros de clima en el rango de fechas indicado
    List<RegistroClima> registros = Arrays.asList(
        crearRegistroClima(1L, "Madrid", "ES", 22.5),
        crearRegistroClima(2L, "Barcelona", "ES", 24.0)
    );
    
    // Configurar el mock para que devuelva la lista simulada cuando se llame al servicio
    when(weatherServiceMock.obtenerHistorialPorFechas(fechaInicio, fechaFin)).thenReturn(registros);
    
    // Act: Ejecutar el método del controlador que obtiene el historial de clima por fechas
    ResponseEntity<?> response = weatherController.getWeatherHistoryByDates(fechaInicio, fechaFin);
    
    // Assert: Verificar que la respuesta tenga código HTTP 200 OK y que el cuerpo no sea nulo
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
}
    
    private RegistroClima crearRegistroClima(Long id, String ciudad, String pais, double temperatura) {
        RegistroClima registro = new RegistroClima();
        registro.setId(id);
        registro.setCiudad(ciudad);
        registro.setPais(pais);
        registro.setTemperatura(temperatura);
        registro.setFechaConsulta(LocalDate.now());
        return registro;
    }
}