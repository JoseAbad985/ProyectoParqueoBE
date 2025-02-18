package est.ups.edu.ec.proyectoparqueo.repository;

import est.ups.edu.ec.proyectoparqueo.model.Placa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlacaRepository extends JpaRepository<Placa, String> {

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Placa p WHERE p.placa = :placa AND p.espacioId IS NOT NULL")
    boolean isPlacaOcupandoEspacio(@Param("placa") String placaId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Contrato c WHERE c.placa.placa = :placa AND c.estado = 'Activo'")
    boolean hasActiveContract(@Param("placa") String placaId);

    @Query("SELECT COUNT(c) FROM Contrato c WHERE c.estado = 'Activo'")
    long countActiveContractSpaces();

    @Query("SELECT p.espacioId FROM Placa p JOIN p.contratos c WHERE c.estado = 'Activo' AND p.espacioId IS NOT NULL " +
            "UNION " +
            "SELECT p.espacioId FROM Placa p JOIN p.tickets t WHERE t.estado = 'En el parqueadero' AND p.espacioId IS NOT NULL")
    List<Integer> getOccupiedSpaces();

    @Modifying
    @Query("UPDATE Placa p SET p.espacioId = NULL WHERE p.placa = :placa")
    void freeUpSpace(@Param("placa") String placa);

    @Query("SELECT p FROM Placa p WHERE p.placa = :placa AND p.estado = :estado")
    Optional<Placa> findByPlacaAndEstado(@Param("placa") String placa, @Param("estado") String estado);



    Optional<Placa> findByPlaca(String placa);
}