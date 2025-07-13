package proyecto.mocasseclima.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.mocasseclima.model.RegistroClima;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroClimaRepository extends JpaRepository<RegistroClima, Long> {
    
    List<RegistroClima> findByCiudadAndPaisOrderByFechaConsultaDesc(String ciudad, String pais);
    
    List<RegistroClima> findByFechaConsultaBetweenOrderByFechaConsultaDesc(
        LocalDate fechaInicio, LocalDate fechaFin);
}