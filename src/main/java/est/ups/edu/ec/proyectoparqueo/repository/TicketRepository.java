package est.ups.edu.ec.proyectoparqueo.repository;

import est.ups.edu.ec.proyectoparqueo.model.Placa;
import est.ups.edu.ec.proyectoparqueo.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // findById, save, delete are automatically provided by JpaRepository

    @Query("SELECT t FROM Ticket t JOIN FETCH t.placa")
    List<Ticket> findAllWithPlaca();

    List<Ticket> findByEstado(String estado);

    @Query("SELECT t FROM Ticket t WHERE t.placa.placa = :placa AND t.estado = 'En el parqueadero'")
    Optional<Ticket> findActivoByPlaca(@Param("placa") String placa);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.estado = 'En el parqueadero'")
    long countActiveTickets();

    @Query("SELECT t FROM Ticket t WHERE t.placa = :placa")
    List<Ticket> findAllByPlaca(Placa placa);
}