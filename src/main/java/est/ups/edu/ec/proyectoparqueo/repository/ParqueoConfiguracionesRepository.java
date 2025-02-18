package est.ups.edu.ec.proyectoparqueo.repository;

import est.ups.edu.ec.proyectoparqueo.model.ParqueoConfiguraciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ParqueoConfiguracionesRepository extends JpaRepository<ParqueoConfiguraciones, Long> {
    @Query("SELECT c FROM ParqueoConfiguraciones c")
    Optional<ParqueoConfiguraciones> findConfiguracion();

    default Optional<ParqueoConfiguraciones> findFirstConfiguration() {
        return findConfiguracion();
    }
}