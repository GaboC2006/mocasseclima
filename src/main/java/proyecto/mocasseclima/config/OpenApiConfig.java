package proyecto.mocasseclima.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI weatherOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MocasseClima API")
                        .description("Microservicio para recuperar información meteorológica mediante OpenWeather API")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Gabriel Castillo Leiton")
                                .email("gabriel.castillo.leiton2006@gmail.com")));
    }
}