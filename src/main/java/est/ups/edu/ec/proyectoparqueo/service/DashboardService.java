package est.ups.edu.ec.proyectoparqueo.service;

import est.ups.edu.ec.proyectoparqueo.model.Placa;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/dashboard")  // Keep it as /dashboard since /api is the context path
@PreAuthorize("isAuthenticated()")
public class DashboardService {
    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);
    private static final String ESTADO_ACTIVO = "En el parqueadero";

    @PersistenceContext
    private EntityManager em;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            int capacidadTotal = em.createQuery(
                            "SELECT c.capacidadMaxima FROM ParqueoConfiguraciones c", Integer.class)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(10);

            long espaciosTickets = em.createQuery(
                            "SELECT COUNT(t) FROM Ticket t WHERE t.estado = :estado", Long.class)
                    .setParameter("estado", ESTADO_ACTIVO)
                    .getSingleResult();

            long espaciosContratos = em.createQuery(
                            "SELECT COUNT(c) FROM Contrato c WHERE c.estado = 'Activo'", Long.class)
                    .getSingleResult();

            // Get ticket spaces
            List<Object[]> ticketInfo = em.createQuery(
                            "SELECT t.placa.placa, t.espacioId FROM Ticket t WHERE t.estado = :estado",
                            Object[].class)
                    .setParameter("estado", ESTADO_ACTIVO)
                    .getResultList();

            List<Map<String, Object>> ticketSpacesFormatted = new ArrayList<>();
            for (Object[] info : ticketInfo) {
                Map<String, Object> spaceMap = new HashMap<>();
                spaceMap.put("placa", info[0]);
                spaceMap.put("spaceId", info[1]);
                ticketSpacesFormatted.add(spaceMap);
            }

            // Get contract spaces
            List<Object[]> contractInfo = em.createQuery(
                            "SELECT p.placa, p.espacioId FROM Placa p " +
                                    "JOIN Contrato c ON c.placa = p " +
                                    "WHERE c.estado = 'Activo' AND p.espacioId IS NOT NULL",
                            Object[].class)
                    .getResultList();

            List<Map<String, Object>> contractSpacesFormatted = new ArrayList<>();
            for (Object[] info : contractInfo) {
                Map<String, Object> spaceMap = new HashMap<>();
                spaceMap.put("placa", info[0]);
                spaceMap.put("spaceId", info[1]);
                contractSpacesFormatted.add(spaceMap);
            }

            Double ingresosDiarios = em.createQuery(
                            "SELECT COALESCE(SUM(t.total), 0) FROM Ticket t WHERE t.estado = 'Facturado' " +
                                    "AND CAST(t.fechaSalida AS date) = CURRENT_DATE", Double.class)
                    .getSingleResult();

            Map<String, Object> parkingState = new HashMap<>();
            parkingState.put("ticketSpaces", ticketSpacesFormatted);
            parkingState.put("contractSpaces", contractSpacesFormatted);

            Map<String, Object> stats = new HashMap<>();
            stats.put("capacidadTotal", capacidadTotal);
            stats.put("espaciosDisponibles", capacidadTotal - (espaciosContratos + espaciosTickets));
            stats.put("ticketsActivos", espaciosTickets);
            stats.put("contratosActivos", espaciosContratos);
            stats.put("ingresosDiarios", ingresosDiarios);
            stats.put("parkingState", parkingState);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error getting dashboard stats", e);
            return ResponseEntity.internalServerError()
                    .body("Error getting dashboard stats: " + e.getMessage());
        }
    }

    @GetMapping("/placas")
    public ResponseEntity<?> getPlacasWithoutActiveTickets() {
        try {
            List<Placa> placas = em.createQuery(
                            "SELECT p FROM Placa p WHERE NOT EXISTS " +
                                    "(SELECT t FROM Ticket t WHERE t.placa = p AND t.estado = :estado)",
                            Placa.class)
                    .setParameter("estado", ESTADO_ACTIVO)
                    .getResultList();
            return ResponseEntity.ok(placas);
        } catch (Exception e) {
            logger.error("Error getting placas", e);
            return ResponseEntity.internalServerError()
                    .body("Error getting placas: " + e.getMessage());
        }
    }

    @GetMapping("/placas/activas")
    public ResponseEntity<?> getPlacasActivas() {
        try {
            List<Placa> activePlacas = em.createQuery(
                            "SELECT p FROM Placa p " +
                                    "WHERE EXISTS (SELECT 1 FROM Ticket t WHERE t.placa = p AND t.estado = :estadoTicket) " +
                                    "OR EXISTS (SELECT 1 FROM Contrato c WHERE c.placa = p AND c.estado = 'Activo')",
                            Placa.class)  // Changed to return full Placa objects
                    .setParameter("estadoTicket", ESTADO_ACTIVO)
                    .getResultList();

            return ResponseEntity.ok(activePlacas);
        } catch (Exception e) {
            logger.error("Error getting active placas", e);
            return ResponseEntity.internalServerError()
                    .body("Error getting active placas: " + e.getMessage());
        }
    }
}