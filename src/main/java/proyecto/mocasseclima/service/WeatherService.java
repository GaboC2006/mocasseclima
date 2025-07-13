package proyecto.mocasseclima.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.net.URLEncoder;

import proyecto.mocasseclima.model.WeatherResponse;
import proyecto.mocasseclima.model.RegistroClima;
import proyecto.mocasseclima.repository.RegistroClimaRepository;
import proyecto.mocasseclima.exception.CityNotFoundException;
import proyecto.mocasseclima.exception.WeatherServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class WeatherService {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    
    @Value("${openweather.api.key}")
    private String claveApi;
    
    @Value("${openweather.api.url}")
    private String urlApi;
    
    private final RestTemplate clienteRest;
    private final RegistroClimaRepository registroClimaRepository;
    
    public WeatherService(RegistroClimaRepository registroClimaRepository) {
        this.clienteRest = new RestTemplate();
        this.registroClimaRepository = registroClimaRepository;
    }
    
    public WeatherResponse obtenerClimaPorCiudad(String ciudad, String codigoPais) {
        try {
            String ciudadCodificada = URLEncoder.encode(ciudad, StandardCharsets.UTF_8);
            String url = String.format("%s?q=%s,%s&appid=%s&units=metric&lang=es", 
                urlApi, ciudadCodificada, codigoPais, claveApi);
            
            logger.info("Realizando petición a OpenWeather para ciudad: {} y país: {}", ciudad, codigoPais);
            
            WeatherResponse response = clienteRest.getForObject(url, WeatherResponse.class);
            
            if (response == null || response.getDatosClimaticos() == null) {
                logger.error("Respuesta nula o inválida para ciudad: {}", ciudad);
                throw new WeatherServiceException("No se encontraron datos para la ciudad: " + ciudad);
            }
            
            // Guardar el registro en la base de datos
            RegistroClima registro = RegistroClima.fromWeatherResponse(response);
            registroClimaRepository.save(registro);
            logger.info("Registro de clima guardado en base de datos para: {}", ciudad);
            
            return response;
        } catch (Exception e) {
            logger.error("Error al obtener datos del clima para {}: {}", ciudad, e.getMessage());
            throw new WeatherServiceException("Error al obtener datos del clima: " + e.getMessage());
        }
    }
    
    public List<RegistroClima> obtenerHistorialClima(String ciudad, String codigoPais) {
        return registroClimaRepository.findByCiudadAndPaisOrderByFechaConsultaDesc(ciudad, codigoPais);
    }
    
    public List<RegistroClima> obtenerHistorialPorFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return registroClimaRepository.findByFechaConsultaBetweenOrderByFechaConsultaDesc(fechaInicio, fechaFin);
    }
}