package est.ups.edu.ec.proyectoparqueo.business;

import est.ups.edu.ec.proyectoparqueo.repository.PlacaRepository;
import est.ups.edu.ec.proyectoparqueo.repository.TicketRepository;
import est.ups.edu.ec.proyectoparqueo.model.Placa;
import est.ups.edu.ec.proyectoparqueo.model.Ticket;
import est.ups.edu.ec.proyectoparqueo.repository.ParqueoConfiguracionesRepository;
import est.ups.edu.ec.proyectoparqueo.model.ParqueoConfiguraciones;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.Duration;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class GestionTickets {

    private final TicketRepository ticketRepository;
    private final PlacaRepository placaRepository;
    private final ParqueoConfiguracionesRepository configRepository;

    public GestionTickets(TicketRepository ticketRepository,
                          PlacaRepository placaRepository,
                          ParqueoConfiguracionesRepository configRepository) {
        this.ticketRepository = ticketRepository;
        this.placaRepository = placaRepository;
        this.configRepository = configRepository;
    }

    public Ticket crearTicket(Placa placa) {
        if (placa == null) {
            throw new IllegalArgumentException("Placa inválida");
        }

        if (placaRepository.hasActiveContract(placa.getPlaca())) {
            throw new IllegalStateException("Esta placa tiene un contrato activo y ya tiene un espacio reservado.");
        }

        if (ticketRepository.findActivoByPlaca(placa.getPlaca()).isPresent()) {
            throw new IllegalStateException("Ya existe un ticket activo para esta placa.");
        }

        Ticket ticket = new Ticket();
        ticket.setPlaca(placa);
        ticket.setFechaIngreso(LocalDateTime.now());
        ticket.setEstado("En el parqueadero");
        ticket.setTipo("temporal");
        ticket.setEspacioId(placa.getEspacioId());
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket completarTicket(String placaId) {
        Ticket ticket = ticketRepository.findActivoByPlaca(placaId)
                .orElseThrow(() -> new IllegalStateException("No existe un ticket activo para esta placa."));

        LocalDateTime fechaSalida = LocalDateTime.now();
        ticket.setFechaSalida(fechaSalida);

        ParqueoConfiguraciones config = configRepository.findConfiguracion()
                .orElseThrow(() -> new IllegalStateException("No se encontró la configuración del parqueadero"));

        // Calculate total time in minutes
        long minutosTotal = Duration.between(ticket.getFechaIngreso(), fechaSalida).toMinutes();

        // Calculate hours and remaining minutes
        long horasCompletas = minutosTotal / 60;
        long minutosRestantes = minutosTotal % 60;

        // If there are any remaining minutes, add an extra hour (minimum 1 hour)
        long horasACobrar = minutosRestantes > 0 ? horasCompletas + 1 : Math.max(1, horasCompletas);

        // Get hourly rate and calculate total
        BigDecimal tarifaPorHora = BigDecimal.valueOf(config.getTarifaPorHora());
        BigDecimal total = tarifaPorHora.multiply(BigDecimal.valueOf(horasACobrar));

        // Round to 2 decimal places
        total = total.setScale(2, RoundingMode.HALF_UP);

        ticket.setTotal(total.doubleValue());
        ticket.setEstado("Facturado");
        ticket = ticketRepository.save(ticket);

        // Get the placa
        Placa placa = ticket.getPlaca();

        // Just mark the placa as inactive and free its space if it doesn't have active contracts
        if (!placaRepository.hasActiveContract(placa.getPlaca())) {
            placa.setEspacioId(0);
            placa.setEstado("Inactivo"); // Add this state to your Placa entity if not already present
            placaRepository.save(placa);
        }

        return ticket;
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%d horas, %d minutos", hours, minutes);
    }
}