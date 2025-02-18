package est.ups.edu.ec.proyectoparqueo.service;

import est.ups.edu.ec.proyectoparqueo.business.GestionTickets;
import est.ups.edu.ec.proyectoparqueo.model.Ticket;
import est.ups.edu.ec.proyectoparqueo.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final GestionTickets gestionTickets;

    public TicketService(TicketRepository ticketRepository, GestionTickets gestionTickets) {
        this.ticketRepository = ticketRepository;
        this.gestionTickets = gestionTickets;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAllWithPlaca();
    }

    @Transactional
    public Ticket registrarSalida(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Ticket not found"));

        String placaId = ticket.getPlaca().getPlaca();
        return gestionTickets.completarTicket(placaId);
    }
}