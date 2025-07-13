package proyecto.mocasseclima.service;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import proyecto.mocasseclima.model.RegistroClima;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class DataFakerService {

    private final Faker faker;
    
    public DataFakerService() {
        this.faker = new Faker(new Locale("es"));
    }
    
    public RegistroClima generarRegistroClima() {
        RegistroClima registro = new RegistroClima();
        
        registro.setCiudad(faker.address().city());
        registro.setPais(faker.address().countryCode());
        registro.setTemperatura(faker.number().randomDouble(2, -10, 45));
        registro.setSensacionTermica(faker.number().randomDouble(2, -15, 50));
        registro.setHumedad(faker.number().numberBetween(0, 100));
        registro.setPresion(faker.number().numberBetween(950, 1050));
        

        String[] condiciones = {"Clear", "Clouds", "Rain", "Thunderstorm", "Snow", "Mist", "Fog"};
        String condicion = condiciones[faker.number().numberBetween(0, condiciones.length)];
        registro.setCondicionClimatica(condicion);
        

        switch (condicion) {
            case "Clear":
                registro.setDescripcionClima("cielo despejado");
                break;
            case "Clouds":
                registro.setDescripcionClima("parcialmente nublado");
                break;
            case "Rain":
                registro.setDescripcionClima("lluvia ligera");
                break;
            case "Thunderstorm":
                registro.setDescripcionClima("tormenta eléctrica");
                break;
            case "Snow":
                registro.setDescripcionClima("nevada");
                break;
            case "Mist":
            case "Fog":
                registro.setDescripcionClima("niebla");
                break;
            default:
                registro.setDescripcionClima("condiciones variables");
        }
        
        registro.setFechaConsulta(LocalDate.now().minusDays(faker.number().numberBetween(0, 30)));
        
        return registro;
    }
    

    public List<RegistroClima> generarRegistrosClima(int cantidad) {
        List<RegistroClima> registros = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            registros.add(generarRegistroClima());
        }
        return registros;
    }
    

    public List<RegistroClima> generarRegistrosParaCiudad(String ciudad, String pais, int cantidad) {
        List<RegistroClima> registros = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            RegistroClima registro = generarRegistroClima();
            registro.setCiudad(ciudad);
            registro.setPais(pais);
            registros.add(registro);
        }
        return registros;
    }
}