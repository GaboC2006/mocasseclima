package proyecto.mocasseclima.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.mocasseclima.model.RegistroClima;
import proyecto.mocasseclima.repository.RegistroClimaRepository;
import proyecto.mocasseclima.service.DataFakerService;

import java.util.List;

@RestController
@RequestMapping("/api/datos-prueba")
public class DataFakerController {

    private final DataFakerService dataFakerService;
    private final RegistroClimaRepository registroClimaRepository;
    
    @Autowired
    public DataFakerController(DataFakerService dataFakerService, RegistroClimaRepository registroClimaRepository) {
        this.dataFakerService = dataFakerService;
        this.registroClimaRepository = registroClimaRepository;
    }
    
    @PostMapping("/generar")
    public ResponseEntity<List<RegistroClima>> generarDatosPrueba(
            @RequestParam(defaultValue = "10") int cantidad) {
        List<RegistroClima> registrosGenerados = dataFakerService.generarRegistrosClima(cantidad);
        List<RegistroClima> registrosGuardados = registroClimaRepository.saveAll(registrosGenerados);
        return ResponseEntity.ok(registrosGuardados);
    }
    
    @PostMapping("/generar/{ciudad}")
    public ResponseEntity<List<RegistroClima>> generarDatosPruebaPorCiudad(
            @PathVariable String ciudad,
            @RequestParam String pais,
            @RequestParam(defaultValue = "5") int cantidad) {
        List<RegistroClima> registrosGenerados = dataFakerService.generarRegistrosParaCiudad(ciudad, pais, cantidad);
        List<RegistroClima> registrosGuardados = registroClimaRepository.saveAll(registrosGenerados);
        return ResponseEntity.ok(registrosGuardados);
    }
}