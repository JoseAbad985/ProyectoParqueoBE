package est.ups.edu.ec.proyectoparqueo.controller;

import est.ups.edu.ec.proyectoparqueo.model.Contrato;
import est.ups.edu.ec.proyectoparqueo.service.ContratoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contratos")
@CrossOrigin(origins = "http://localhost:4200")
public class ContratoController {

    private final ContratoService contratoService;

    @Autowired
    public ContratoController(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    @GetMapping("/next-space")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<Integer> getNextAvailableSpace() {
        try {
            int space = contratoService.findAvailableSpaceId();
            return ResponseEntity.ok(space);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/espacios-disponibles")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<?> getEspaciosDisponibles() {
        return contratoService.getEspaciosDisponibles();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<?> getAllContratos() {
        return contratoService.getAllContratos();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<?> createContrato(@RequestBody Contrato contrato) {
        return contratoService.createContrato(contrato);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<?> getContrato(@PathVariable Long id) {
        return contratoService.getContrato(id);
    }

    @GetMapping("/usuario/{cedula}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<?> getContratosByUsuario(@PathVariable String cedula) {
        return contratoService.getContratosByUsuario(cedula);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<?> updateContrato(@PathVariable Long id, @RequestBody Contrato contrato) {
        return contratoService.updateContrato(id, contrato);
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        return contratoService.cambiarEstado(id, estado);
    }
}