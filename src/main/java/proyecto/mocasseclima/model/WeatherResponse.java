package proyecto.mocasseclima.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Data
public class WeatherResponse {
    @JsonProperty("main")
    private DatosClimaticos datosClimaticos;
    @JsonProperty("weather")
    private List<Clima> condicionesClimaticas;
    @JsonProperty("name")
    private String nombreCiudad;
    @JsonProperty("sys")
    private Sistema sistema;
    
    @Data
    public static class DatosClimaticos {
        @JsonProperty("temp")
        private double temperatura;
        @JsonProperty("feels_like")
        private double sensacionTermica;
        @JsonProperty("temp_min")
        private double temperaturaMinima;
        @JsonProperty("temp_max")
        private double temperaturaMaxima;
        @JsonProperty("humidity")
        private int humedad;
        @JsonProperty("pressure")
        private int presion;
    }
    
    @Data
    public static class Clima {
        @JsonProperty("id")
        private int identificador;
        @JsonProperty("main")
        private String condicionPrincipal;
        @JsonProperty("description")
        private String descripcion;
        @JsonProperty("icon")
        private String icono;
    }

    @Data
    public static class Sistema {
        @JsonProperty("country")
        private String pais;
        @JsonProperty("sunrise")
        private long amanecer;
        @JsonProperty("sunset")
        private long atardecer;
        
        public LocalDateTime getHoraAmanecer() {
            return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(amanecer), 
                ZoneId.systemDefault()
            );
        }
        
        public LocalDateTime getHoraAtardecer() {
            return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(atardecer), 
                ZoneId.systemDefault()
            );
        }
    }
}