package est.ups.edu.ec.proyectoparqueo.repository;

import est.ups.edu.ec.proyectoparqueo.model.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    List<Contrato> findByUsuarioCedula(String cedulaUsuario);

    @Modifying
    @Query("UPDATE Contrato c SET c.estado = 'Anulado' WHERE c.id = :id")
    void anularContrato(@Param("id") Long id);
}