package est.ups.edu.ec.proyectoparqueo.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "placa_id", nullable = false)
    private Placa placa;

    @Column(name = "fecha_ingreso", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_salida")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaSalida;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false, name = "total")
    private double total;

    @Column(nullable = false)
    private String tipo = "temporal";

    @Column(name = "espacio_id")
    private Integer espacioId;

    public Ticket() {
        this.fechaIngreso = LocalDateTime.now();
        this.estado = "En el parqueadero";
        this.tipo = "temporal";
        this.total = 0.0;
    }

    public Ticket(Placa placa) {
        this.placa = placa;
        this.fechaIngreso = LocalDateTime.now();
        this.estado = "En el parqueadero";
        this.tipo = "temporal";
        this.total = 0.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Placa getPlaca() {
        return placa;
    }

    public void setPlaca(Placa placa) {
        this.placa = placa;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getEspacioId() {
        return espacioId;
    }

    public void setEspacioId(Integer espacioId) {
        this.espacioId = espacioId;
    }
}