package est.ups.edu.ec.proyectoparqueo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import est.ups.edu.ec.proyectoparqueo.model.Contrato;
import est.ups.edu.ec.proyectoparqueo.model.User;
import est.ups.edu.ec.proyectoparqueo.model.Placa;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ContratoService {
    private static final Logger logger = LoggerFactory.getLogger(ContratoService.class);

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public ResponseEntity<?> getAllContratos() {
        try {
            TypedQuery<Contrato> query = em.createQuery(
                    "SELECT c FROM Contrato c " +
                            "LEFT JOIN FETCH c.placa " +
                            "LEFT JOIN FETCH c.usuario",
                    Contrato.class
            );

            List<Contrato> contratos = query.getResultList();
            return ResponseEntity.ok(contratos);
        } catch (Exception e) {
            logger.error("Error loading contratos", e);
            return ResponseEntity.internalServerError()
                    .body("Error al cargar contratos: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> getEspaciosDisponibles() {
        try {
            long totalSpaces = 12;
            long occupiedSpaces = em.createQuery(
                    "SELECT COUNT(DISTINCT c.placa) FROM Contrato c WHERE c.estado = 'Activo'",
                    Long.class
            ).getSingleResult();

            long availableSpaces = totalSpaces - occupiedSpaces;

            Map<String, Object> response = new HashMap<>();
            response.put("total", totalSpaces);
            response.put("disponibles", availableSpaces);
            response.put("ocupados", occupiedSpaces);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting espacios disponibles", e);
            return ResponseEntity.internalServerError()
                    .body("Error al obtener espacios disponibles: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> createContrato(Contrato contrato) {
        try {
            logger.info("Creating contract for placa: {}", contrato.getPlaca().getPlaca());

            // First, try to find the Placa by its plate number string
            Placa placa = em.createQuery(
                            "SELECT p FROM Placa p WHERE p.placa = :placa",
                            Placa.class)
                    .setParameter("placa", contrato.getPlaca().getPlaca())
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(null);

            // If plate doesn't exist, create a new one
            if (placa == null) {
                placa = new Placa();
                placa.setPlaca(contrato.getPlaca().getPlaca());
                placa.setEstado("Activo");
                placa.setFechaInicio(LocalDateTime.now());
                em.persist(placa);
                em.flush();
            }

            // Find the user
            User usuario = em.find(User.class, contrato.getUsuario().getCedula());
            if (usuario == null) {
                logger.error("User not found: {}", contrato.getUsuario().getCedula());
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            // Check if plate already has an active contract
            Long activeContractsCount = em.createQuery(
                            "SELECT COUNT(c) FROM Contrato c WHERE c.placa = :placa AND c.estado = 'Activo'",
                            Long.class)
                    .setParameter("placa", placa)
                    .getSingleResult();

            if (activeContractsCount > 0) {
                logger.warn("Placa already has active contract: {}", placa.getPlaca());
                return ResponseEntity.badRequest().body("La placa ya está asignada a un contrato activo");
            }

            // Check total active contracts (parking capacity)
            Long totalActiveContracts = em.createQuery(
                            "SELECT COUNT(c) FROM Contrato c WHERE c.estado = 'Activo'",
                            Long.class)
                    .getSingleResult();

            if (totalActiveContracts >= 12) {
                logger.warn("No parking spaces available");
                return ResponseEntity.badRequest().body("No hay espacios de parqueo disponibles");
            }

            // Find and assign parking space
            int espacioId = findAvailableSpaceId();
            placa.setEspacioId(espacioId);
            placa = em.merge(placa);
            em.flush();

            // Create the new contract
            Contrato nuevoContrato = new Contrato();
            nuevoContrato.setPlaca(placa);
            nuevoContrato.setUsuario(usuario);
            nuevoContrato.setDescripcion(contrato.getDescripcion());
            nuevoContrato.setTarifaContrato(contrato.getTarifaContrato());
            nuevoContrato.setEstado("Activo");

            // Set the dates from the request if provided, otherwise use defaults
            nuevoContrato.setFechaInicio(contrato.getFechaInicio() != null ?
                    contrato.getFechaInicio() : LocalDateTime.now());
            nuevoContrato.setFechaFin(contrato.getFechaFin() != null ?
                    contrato.getFechaFin() : nuevoContrato.getFechaInicio().plusMonths(1));

            em.persist(nuevoContrato);
            em.flush();

            logger.info("Contract created successfully for placa: {}", placa.getPlaca());
            return ResponseEntity.ok(nuevoContrato);
        } catch (Exception e) {
            logger.error("Error creating contract", e);
            return ResponseEntity.internalServerError()
                    .body("Error al crear contrato: " + e.getMessage());
        }
    }

    @Transactional
    public int findAvailableSpaceId() {
        List<Integer> occupiedSpaces = em.createQuery(
                "SELECT DISTINCT p.espacioId FROM Placa p " +
                        "JOIN Contrato c ON c.placa = p WHERE c.estado = 'Activo' AND p.espacioId > 0",
                Integer.class
        ).getResultList();

        for (int i = 1; i <= 12; i++) {
            if (!occupiedSpaces.contains(i)) {
                return i;
            }
        }

        throw new IllegalStateException("No hay espacios de parqueo disponibles");
    }

    @Transactional
    public ResponseEntity<?> getContrato(Long id) {
        try {
            Contrato contrato = em.find(Contrato.class, id);
            return contrato != null ? ResponseEntity.ok(contrato) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting contract", e);
            return ResponseEntity.internalServerError()
                    .body("Error al obtener contrato: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> getContratosByUsuario(String cedula) {
        try {
            List<Contrato> contratos = em.createQuery(
                            "SELECT c FROM Contrato c WHERE c.usuario.cedula = :cedula",
                            Contrato.class)
                    .setParameter("cedula", cedula)
                    .getResultList();
            return ResponseEntity.ok(contratos);
        } catch (Exception e) {
            logger.error("Error getting contracts by user", e);
            return ResponseEntity.internalServerError()
                    .body("Error al obtener contratos del usuario: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> updateContrato(Long id, Contrato contrato) {
        try {
            Contrato existing = em.find(Contrato.class, id);
            if(existing == null) return ResponseEntity.notFound().build();

            existing.setDescripcion(contrato.getDescripcion());
            existing.setTarifaContrato(contrato.getTarifaContrato());
            em.merge(existing);
            em.flush();
            return ResponseEntity.ok(existing);
        } catch (Exception e) {
            logger.error("Error updating contract", e);
            return ResponseEntity.internalServerError()
                    .body("Error al actualizar contrato: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> cambiarEstado(Long id, String estado) {
        try {
            Contrato contrato = em.find(Contrato.class, id);
            if(contrato == null) return ResponseEntity.notFound().build();

            if ("Cancelado".equals(estado) || "Expirado".equals(estado)) {
                Placa placa = contrato.getPlaca();
                placa.setEspacioId(null);
                em.merge(placa);
            }

            contrato.setEstado(estado);
            em.merge(contrato);
            em.flush();
            return ResponseEntity.ok(contrato);
        } catch (Exception e) {
            logger.error("Error changing contract status", e);
            return ResponseEntity.internalServerError()
                    .body("Error al cambiar estado del contrato: " + e.getMessage());
        }
    }
}