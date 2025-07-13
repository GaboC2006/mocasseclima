package proyecto.mocasseclima.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import proyecto.mocasseclima.exception.CityNotFoundException;
import proyecto.mocasseclima.exception.WeatherServiceException;
import proyecto.mocasseclima.model.RegistroClima;
import proyecto.mocasseclima.model.WeatherResponse;
import proyecto.mocasseclima.repository.RegistroClimaRepository;
import proyecto.mocasseclima.controller.WeatherController;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplateMock;

    @Mock
    private RegistroClimaRepository repositoryMock;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        // Configurar propiedades mediante ReflectionTestUtils ya que no podemos usar @Value en pruebas unitarias
        ReflectionTestUtils.setField(weatherService, "claveApi", "test-api-key");
        ReflectionTestUtils.setField(weatherService, "urlApi", "http://test-api.com/data");
        
        // Reemplazar el RestTemplate creado en el constructor con nuestro mock
        ReflectionTestUtils.setField(weatherService, "clienteRest", restTemplateMock);
    }

    @Test
    void obtenerClimaPorCiudad_DeberiaRetornarDatosClima_CuandoLaConsultaEsExitosa() {
        // Arrange: Definir ciudad y país para la consulta
        String ciudad = "Madrid";
        String pais = "ES";
        
        // Crear una respuesta simulada con datos climáticos completos
        WeatherResponse mockResponse = new WeatherResponse();
        mockResponse.setNombreCiudad(ciudad);
        
        WeatherResponse.DatosClimaticos datosClimaticos = new WeatherResponse.DatosClimaticos();
        datosClimaticos.setTemperatura(20.5);
        datosClimaticos.setHumedad(65);
        mockResponse.setDatosClimaticos(datosClimaticos);
        
        WeatherResponse.Sistema sistema = new WeatherResponse.Sistema();
        sistema.setPais(pais);
        mockResponse.setSistema(sistema);
        
        WeatherResponse.Clima clima = new WeatherResponse.Clima();
        clima.setCondicionPrincipal("Clear");
        clima.setDescripcion("Cielo despejado");
        mockResponse.setCondicionesClimaticas(Arrays.asList(clima));
        
        // Configurar el mock del RestTemplate para que devuelva la respuesta simulada
        when(restTemplateMock.getForObject(anyString(), eq(WeatherResponse.class))).thenReturn(mockResponse);
        
        // Act: Ejecutar el método del servicio que obtiene el clima por ciudad
        WeatherResponse result = weatherService.obtenerClimaPorCiudad(ciudad, pais);
        
        // Assert: Verificar que el resultado no sea nulo, que los datos coincidan y que se guarde el registro
        assertNotNull(result, "La respuesta no debe ser nula");
        assertEquals(ciudad, result.getNombreCiudad(), "El nombre de la ciudad debe coincidir");
        assertEquals(20.5, result.getDatosClimaticos().getTemperatura(), "La temperatura debe coincidir");
        
        // Verificar que el repositorio haya guardado un registro de clima una vez
        verify(repositoryMock, times(1)).save(any(RegistroClima.class));
    }
    

    @Test
    void obtenerClimaPorCiudad_DeberiaManejarCiudadNoEncontrada() {
        // Arrange: Definir ciudad y país inexistentes para la prueba
        String ciudad = "CiudadInexistente";
        String pais = "XX";
        
        // Configurar el mock del RestTemplate para que devuelva null simulando que la ciudad no fue encontrada
        when(restTemplateMock.getForObject(anyString(), eq(WeatherResponse.class))).thenReturn(null);
        
        // Act & Assert: Ejecutar el método y verificar que lance una excepción WeatherServiceException
        WeatherServiceException exception = assertThrows(WeatherServiceException.class, () -> {
            weatherService.obtenerClimaPorCiudad(ciudad, pais);
        });
        
        // Verificar que el mensaje de la excepción contenga información sobre la ciudad no encontrada
        assertTrue(exception.getMessage().contains("No se encontraron datos para la ciudad"));
        
        // Verificar que no se haya intentado guardar ningún registro en la base de datos
        verify(repositoryMock, never()).save(any());
    }

    @Test
    void obtenerClimaPorCiudad_DeberiaManejarErroresDeServicio() {
        // Arrange: Definir ciudad y país para la prueba
        String ciudad = "Madrid";
        String pais = "ES";
        
        // Configurar el mock del RestTemplate para que lance una excepción simulando un error de conexión
        when(restTemplateMock.getForObject(anyString(), eq(WeatherResponse.class)))
            .thenThrow(new RuntimeException("Error de conexión"));
        
        // Act & Assert: Verificar que el método lance una excepción WeatherServiceException ante el error
        assertThrows(WeatherServiceException.class, () -> {
            weatherService.obtenerClimaPorCiudad(ciudad, pais);
        });
        
        // Verificar que no se haya intentado guardar ningún registro en la base de datos
        verify(repositoryMock, never()).save(any());
    }

    @Test
    void obtenerHistorialClima_DeberiaRetornarRegistrosDeBaseDeDatos() {
        // Arrange: Definir ciudad y país para la consulta del historial
        String ciudad = "Madrid";
        String pais = "ES";
        
        // Crear una lista simulada de registros de clima para la ciudad y país indicados
        List<RegistroClima> registrosEsperados = Arrays.asList(
            crearRegistroClima(1L, ciudad, pais, 22.5),
            crearRegistroClima(2L, ciudad, pais, 23.0)
        );
        
        // Configurar el mock del repositorio para que devuelva la lista simulada cuando se consulte por ciudad y país
        when(repositoryMock.findByCiudadAndPaisOrderByFechaConsultaDesc(ciudad, pais))
            .thenReturn(registrosEsperados);
        
        // Act: Ejecutar el método del servicio que obtiene el historial de clima
        List<RegistroClima> resultado = weatherService.obtenerHistorialClima(ciudad, pais);
        
        // Assert: Verificar que la lista devuelta tenga el tamaño esperado y que los datos coincidan
        assertEquals(2, resultado.size(), "Debe retornar dos registros");
        assertEquals(ciudad, resultado.get(0).getCiudad(), "La ciudad del primer registro debe coincidir");
        assertEquals(pais, resultado.get(0).getPais(), "El país del primer registro debe coincidir");
    }

    @Test
    void obtenerHistorialPorFechas_DeberiaRetornarRegistrosEnRangoDeFechas() {
        // Arrange: Definir el rango de fechas para la consulta (últimos 7 días hasta hoy)
        LocalDate fechaInicio = LocalDate.now().minusDays(7);
        LocalDate fechaFin = LocalDate.now();
        
        // Crear una lista simulada de registros de clima dentro del rango de fechas definido
        List<RegistroClima> registrosEsperados = Arrays.asList(
            crearRegistroClima(1L, "Madrid", "ES", 22.5),
            crearRegistroClima(2L, "Barcelona", "ES", 24.0)
        );
        
        // Configurar el mock del repositorio para que devuelva la lista simulada
        // cuando se consulte por registros entre fechaInicio y fechaFin ordenados descendentemente
        when(repositoryMock.findByFechaConsultaBetweenOrderByFechaConsultaDesc(fechaInicio, fechaFin))
            .thenReturn(registrosEsperados);
        
        // Act: Ejecutar el método del servicio que obtiene el historial de clima en el rango de fechas
        List<RegistroClima> resultado = weatherService.obtenerHistorialPorFechas(fechaInicio, fechaFin);
        
        // Assert: Verificar que la lista devuelta tenga el tamaño esperado (2 registros)
        assertEquals(2, resultado.size(), "Debe retornar dos registros dentro del rango de fechas");
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