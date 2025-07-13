package proyecto.mocasseclima.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "registros_clima")
@Data
@NoArgsConstructor
public class RegistroClima {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String ciudad;
    private String pais;
    private double temperatura;
    private double sensacionTermica;
    private int humedad;
    private int presion;
    private String condicionClimatica;
    private String descripcionClima;
    
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaConsulta;
    
    public static RegistroClima fromWeatherResponse(WeatherResponse response) {
        RegistroClima registro = new RegistroClima();
        registro.setCiudad(response.getNombreCiudad());
        registro.setPais(response.getSistema().getPais());
        registro.setTemperatura(response.getDatosClimaticos().getTemperatura());
        registro.setSensacionTermica(response.getDatosClimaticos().getSensacionTermica());
        registro.setHumedad(response.getDatosClimaticos().getHumedad());
        registro.setPresion(response.getDatosClimaticos().getPresion());
        
        if (!response.getCondicionesClimaticas().isEmpty()) {
            WeatherResponse.Clima clima = response.getCondicionesClimaticas().get(0);
            registro.setCondicionClimatica(clima.getCondicionPrincipal());
            registro.setDescripcionClima(clima.getDescripcion());
        }
        
        registro.setFechaConsulta(LocalDate.now());
        return registro;
    }
}