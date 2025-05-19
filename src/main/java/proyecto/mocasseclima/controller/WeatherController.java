package proyecto.mocasseclima.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.mocasseclima.exception.CityNotFoundException;
import proyecto.mocasseclima.exception.WeatherServiceException;
import proyecto.mocasseclima.model.WeatherResponse;
import proyecto.mocasseclima.service.WeatherService;

@RestController
@RequestMapping("/api/clima")
public class WeatherController {
    
    private final WeatherService weatherService;
    
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }
    
    @GetMapping("/{ciudad}")
    public ResponseEntity<WeatherResponse> getWeatherByCity(
            @PathVariable String ciudad,
            @RequestParam(required = true) String pais) {
        try {
            WeatherResponse response = weatherService.obtenerClimaPorCiudad(ciudad, pais);
            return ResponseEntity.ok(response);
        } catch (CityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (WeatherServiceException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return ResponseEntity.internalServerError().body("Error interno del servidor");
    }
}