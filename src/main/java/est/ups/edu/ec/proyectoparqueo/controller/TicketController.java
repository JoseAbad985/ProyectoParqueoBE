package est.ups.edu.ec.proyectoparqueo.controller;

import est.ups.edu.ec.proyectoparqueo.model.Ticket;
import est.ups.edu.ec.proyectoparqueo.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/{id}/salida")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
    public ResponseEntity<?> registrarSalida(@PathVariable Long id) {
        try {
            Ticket updatedTicket = ticketService.registrarSalida(id);
            return ResponseEntity.ok(updatedTicket);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar salida: " + e.getMessage());
        }
    }
}