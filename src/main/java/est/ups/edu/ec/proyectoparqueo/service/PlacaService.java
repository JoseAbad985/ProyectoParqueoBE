package est.ups.edu.ec.proyectoparqueo.service;

import est.ups.edu.ec.proyectoparqueo.model.Placa;
import est.ups.edu.ec.proyectoparqueo.model.Ticket;
import est.ups.edu.ec.proyectoparqueo.business.GestionTickets;
import est.ups.edu.ec.proyectoparqueo.repository.PlacaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/placas")  // Keep it as /placas since /api is the context path
@Transactional
public class PlacaService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private GestionTickets gestionTickets;

    @Autowired
    private PlacaRepository placaRepository;

    @PostMapping
    public ResponseEntity<?> createPlaca(@RequestBody Placa placa) {
        try {
            if (placa.getPlaca() == null || placa.getPlaca().isEmpty() || placa.getPlaca().length() > 8) {
                return ResponseEntity.badRequest().body("Placa inválida");
            }

            Optional<Placa> existingPlaca = placaRepository.findByPlacaAndEstado(placa.getPlaca(), "Inactivo");
            if (existingPlaca.isPresent()) {
                // Placa exists and is inactive
                // Reuse the existing placa
                Placa placaToReuse = existingPlaca.get();
                int nextSpace = getNextAvailableSpaceId();
                placaToReuse.setEspacioId(nextSpace);
                placaToReuse.setFechaInicio(LocalDateTime.now());
                placaToReuse.setEstado("Activo");
                em.merge(placaToReuse);
                em.flush();
                em.refresh(placaToReuse);

                Ticket ticket = gestionTickets.crearTicket(placaToReuse);

                return ResponseEntity.ok(placaToReuse);
            } else {
                // Placa doesn't exist or is currently active
                int nextSpace = getNextAvailableSpaceId();
                placa.setEspacioId(nextSpace);
                placa.setFechaInicio(LocalDateTime.now());
                placa.setEstado("Activo");

                em.persist(placa);
                em.flush();
                em.refresh(placa);

                Ticket ticket = gestionTickets.crearTicket(placa);

                return ResponseEntity.ok(placa);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error al crear la placa: " + e.getMessage());
        }
    }

    private int getNextAvailableSpaceId() {
        List<Integer> occupiedSpaces = em.createQuery(
                "SELECT p.espacioId FROM Placa p WHERE p.espacioId > 0",
                Integer.class
        ).getResultList();

        for (int i = 1; i <= 10; i++) {
            if (!occupiedSpaces.contains(i)) {
                return i;
            }
        }
        throw new RuntimeException("No hay espacios disponibles");
    }

    @GetMapping("/{placa}")
    public ResponseEntity<?> getPlaca(@PathVariable String placa) {
        try {
            Placa p = em.find(Placa.class, placa);
            return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPlacas() {
        try {
            List<Placa> placas = em.createQuery("SELECT p FROM Placa p", Placa.class)
                    .getResultList();
            return ResponseEntity.ok(placas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/{placa}")
    public ResponseEntity<?> updatePlaca(@PathVariable String placa, @RequestBody Placa p) {
        try {
            Placa existing = em.find(Placa.class, placa);
            if (existing == null) {
                return ResponseEntity.notFound().build();
            }

            existing.setEspacioId(p.getEspacioId());
            em.merge(existing);
            return ResponseEntity.ok(existing);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/{placa}")
    public ResponseEntity<?> deletePlaca(@PathVariable String placa) {
        try {
            Placa p = em.find(Placa.class, placa);
            if (p != null) {
                em.remove(p);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/activas")
    public ResponseEntity<?> getPlacasActivas() {
        try {
            List<Placa> activePlacas = em.createQuery(
                    "SELECT p FROM Placa p WHERE p.estado = 'Activo'",
                    Placa.class
            ).getResultList();

            return ResponseEntity.ok(activePlacas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al obtener placas activas: " + e.getMessage());
        }
    }

    @GetMapping("/without-active-tickets")
    public ResponseEntity<?> getPlacasWithoutActiveTickets() {
        try {
            List<Placa> placasWithoutActiveTickets = em.createQuery(
                    "SELECT p FROM Placa p WHERE p.espacioId IS NOT NULL AND NOT EXISTS (" +
                            "SELECT t FROM Ticket t WHERE t.placa = p AND t.estado = 'En el parqueadero')",
                    Placa.class
            ).getResultList();

            return ResponseEntity.ok(placasWithoutActiveTickets);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al obtener placas sin tickets activos: " + e.getMessage());
        }
    }
}