package proyecto.mocasseclima.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import proyecto.mocasseclima.model.RegistroClima;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class RegistroClimaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RegistroClimaRepository registroClimaRepository;

    @Test
    void findByCiudadAndPaisOrderByFechaConsultaDesc_DeberiaRetornarRegistrosOrdenados() {
        // Arrange: Crear registros de clima para diferentes ciudades y fechas
        RegistroClima registro1 = crearRegistroClima("Madrid", "ES", 22.5, LocalDate.now().minusDays(1));
        RegistroClima registro2 = crearRegistroClima("Madrid", "ES", 23.0, LocalDate.now());
        RegistroClima registro3 = crearRegistroClima("Barcelona", "ES", 24.0, LocalDate.now());
        
        // Persistir los registros en la base de datos de prueba
        entityManager.persist(registro1);
        entityManager.persist(registro2);
        entityManager.persist(registro3);
        entityManager.flush();
        
        // Act: Consultar registros para Madrid, ES, ordenados por fecha descendente
        List<RegistroClima> resultado = registroClimaRepository.findByCiudadAndPaisOrderByFechaConsultaDesc("Madrid", "ES");
        
        // Assert: Verificar que se obtienen solo los registros de Madrid y que están ordenados correctamente
        assertEquals(2, resultado.size(), "Debe retornar dos registros para Madrid");
        assertEquals(registro2.getFechaConsulta(), resultado.get(0).getFechaConsulta(), "El registro más reciente debe estar primero");
    }

    @Test
    void findByFechaConsultaBetweenOrderByFechaConsultaDesc_DeberiaRetornarRegistrosEnRangoDeFechas() {
        // Arrange: Definir fechas para el rango de consulta
        LocalDate hoy = LocalDate.now();
        LocalDate ayer = hoy.minusDays(1);
        LocalDate anteayer = hoy.minusDays(2);
        LocalDate haceTresDias = hoy.minusDays(3);
        
        // Crear registros de clima con diferentes fechas
        RegistroClima registro1 = crearRegistroClima("Madrid", "ES", 22.5, ayer);
        RegistroClima registro2 = crearRegistroClima("Barcelona", "ES", 23.0, hoy);
        RegistroClima registro3 = crearRegistroClima("Valencia", "ES", 24.0, anteayer);
        RegistroClima registro4 = crearRegistroClima("Sevilla", "ES", 25.0, haceTresDias);
        
        // Persistir los registros en la base de datos
        entityManager.persist(registro1);
        entityManager.persist(registro2);
        entityManager.persist(registro3);
        entityManager.persist(registro4);
        entityManager.flush();
        
        // Act: Consultar registros entre anteayer y hoy, ordenados por fecha descendente
        List<RegistroClima> resultado = registroClimaRepository.findByFechaConsultaBetweenOrderByFechaConsultaDesc(
            anteayer, hoy);
        
        // Assert: Verificar que se obtienen 3 registros y que el más reciente está primero
        assertEquals(3, resultado.size(), "Debe retornar tres registros en el rango de fechas");
        assertEquals(hoy, resultado.get(0).getFechaConsulta(), "El registro más reciente debe estar primero");
    }
    
    private RegistroClima crearRegistroClima(String ciudad, String pais, double temperatura, LocalDate fecha) {
        RegistroClima registro = new RegistroClima();
        registro.setCiudad(ciudad);
        registro.setPais(pais);
        registro.setTemperatura(temperatura);
        registro.setHumedad(65);
        registro.setPresion(1013);
        registro.setCondicionClimatica("Clear");
        registro.setDescripcionClima("Cielo despejado");
        registro.setFechaConsulta(fecha);
        return registro;
    }
}