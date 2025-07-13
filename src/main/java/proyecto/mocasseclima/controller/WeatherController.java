package proyecto.mocasseclima.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.mocasseclima.exception.CityNotFoundException;
import proyecto.mocasseclima.exception.WeatherServiceException;
import proyecto.mocasseclima.model.RegistroClima;
import proyecto.mocasseclima.model.WeatherResponse;
import proyecto.mocasseclima.service.WeatherService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/clima")
public class WeatherController {
    
    private final WeatherService weatherService;
    
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }
    
    @GetMapping("/{ciudad}")
    public ResponseEntity<EntityModel<WeatherResponse>> getWeatherByCity(
            @PathVariable String ciudad,
            @RequestParam(required = true) String pais) {
        try {
            WeatherResponse response = weatherService.obtenerClimaPorCiudad(ciudad, pais);
            
            EntityModel<WeatherResponse> resource = EntityModel.of(response);
            
            Link selfLink = linkTo(methodOn(WeatherController.class)
                    .getWeatherByCity(ciudad, pais))
                    .withSelfRel();
            
            Link historialLink = linkTo(methodOn(WeatherController.class)
                    .getWeatherHistory(ciudad, pais))
                    .withRel("historial");
            
            resource.add(selfLink, historialLink);
            
            return ResponseEntity.ok(resource);
        } catch (CityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (WeatherServiceException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/historial/{ciudad}")
    public ResponseEntity<CollectionModel<EntityModel<RegistroClima>>> getWeatherHistory(
            @PathVariable String ciudad,
            @RequestParam(required = true) String pais) {
        List<RegistroClima> historial = weatherService.obtenerHistorialClima(ciudad, pais);
        
        // Convertir cada registro a un EntityModel con enlaces
        List<EntityModel<RegistroClima>> registroResources = historial.stream()
                .map(registro -> {
                    EntityModel<RegistroClima> resource = EntityModel.of(registro);
                    
                    // Enlace al clima actual de esta ciudad
                    Link climaActualLink = linkTo(methodOn(WeatherController.class)
                            .getWeatherByCity(registro.getCiudad(), registro.getPais()))
                            .withRel("clima-actual");
                    
                    resource.add(climaActualLink);
                    return resource;
                })
                .collect(Collectors.toList());
        
        // Crear el modelo de colección con enlaces adicionales
        CollectionModel<EntityModel<RegistroClima>> collectionModel = 
                CollectionModel.of(registroResources);
        
        // Enlace a sí mismo
        Link selfLink = linkTo(methodOn(WeatherController.class)
                .getWeatherHistory(ciudad, pais))
                .withSelfRel();
        
        // Enlace para generar datos de prueba para esta ciudad
        Link generarDatosLink = linkTo(methodOn(DataFakerController.class)
                .generarDatosPruebaPorCiudad(ciudad, pais, 5))
                .withRel("generar-datos-prueba");
        
        collectionModel.add(selfLink, generarDatosLink);
        
        return ResponseEntity.ok(collectionModel);
    }
    
    @GetMapping("/historial/fechas")
    public ResponseEntity<CollectionModel<EntityModel<RegistroClima>>> getWeatherHistoryByDates(
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaFin) {
        List<RegistroClima> historial = weatherService.obtenerHistorialPorFechas(fechaInicio, fechaFin);
        
        // Convertir cada registro a un EntityModel con enlaces
        List<EntityModel<RegistroClima>> registroResources = historial.stream()
                .map(registro -> {
                    EntityModel<RegistroClima> resource = EntityModel.of(registro);
                    
                    // Enlace al clima actual de esta ciudad
                    Link climaActualLink = linkTo(methodOn(WeatherController.class)
                            .getWeatherByCity(registro.getCiudad(), registro.getPais()))
                            .withRel("clima-actual");
                    
                    // Enlace al historial de esta ciudad
                    Link historialCiudadLink = linkTo(methodOn(WeatherController.class)
                            .getWeatherHistory(registro.getCiudad(), registro.getPais()))
                            .withRel("historial-ciudad");
                    
                    resource.add(climaActualLink, historialCiudadLink);
                    return resource;
                })
                .collect(Collectors.toList());
        
        // Crear el modelo de colección con enlaces adicionales
        CollectionModel<EntityModel<RegistroClima>> collectionModel = 
                CollectionModel.of(registroResources);
        
        // Enlace a sí mismo
        Link selfLink = linkTo(methodOn(WeatherController.class)
                .getWeatherHistoryByDates(fechaInicio, fechaFin))
                .withSelfRel();
        
        // Enlace para generar datos de prueba generales
        Link generarDatosLink = linkTo(methodOn(DataFakerController.class)
                .generarDatosPrueba(10))
                .withRel("generar-datos-prueba");
        
        collectionModel.add(selfLink, generarDatosLink);
        
        return ResponseEntity.ok(collectionModel);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return ResponseEntity.internalServerError().body("Error interno del servidor");
    }
}