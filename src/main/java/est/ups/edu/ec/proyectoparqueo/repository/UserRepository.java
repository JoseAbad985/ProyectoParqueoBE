package est.ups.edu.ec.proyectoparqueo.repository;

import est.ups.edu.ec.proyectoparqueo.model.User;
import est.ups.edu.ec.proyectoparqueo.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByCedula(String cedula);

    @Query("SELECT COUNT(u) FROM User u WHERE u.rol = :role AND u.activo = true")
    long countActiveAdmins(@Param("role") UserRole role);
    //19-2-2025=5:27pm
    @Query("SELECT DISTINCT u.email FROM User u " +
            "JOIN Contrato c ON c.usuario = u " +
            "WHERE c.estado = 'Activo' AND u.activo = true")
    List<String> findEmailsOfUsersWithActiveContracts();
}
