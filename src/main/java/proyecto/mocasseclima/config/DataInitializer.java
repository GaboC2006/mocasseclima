package proyecto.mocasseclima.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import proyecto.mocasseclima.model.RegistroClima;
import proyecto.mocasseclima.repository.RegistroClimaRepository;
import proyecto.mocasseclima.service.DataFakerService;

import java.util.List;

@Configuration
public class DataInitializer {


    @Bean
    @Profile("dev")
    public CommandLineRunner initData(DataFakerService dataFakerService, RegistroClimaRepository repository) {
        return args -> {

            if (repository.count() == 0) {

                List<RegistroClima> registrosAleatorios = dataFakerService.generarRegistrosClima(20);
                repository.saveAll(registrosAleatorios);
                

                List<RegistroClima> registrosChile = dataFakerService.generarRegistrosParaCiudad("Santiago", "CL", 5);
                List<RegistroClima> registrosBuenosAires = dataFakerService.generarRegistrosParaCiudad("Buenos Aires", "AR", 5);
                List<RegistroClima> registrosMexico = dataFakerService.generarRegistrosParaCiudad("Ciudad de México", "MX", 5);
                
                repository.saveAll(registrosChile);
                repository.saveAll(registrosBuenosAires);
                repository.saveAll(registrosMexico);
                
                System.out.println("Se han generado datos de prueba iniciales");
            }
        };
    }
}