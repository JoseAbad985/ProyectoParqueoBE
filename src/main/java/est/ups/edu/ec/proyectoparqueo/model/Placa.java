package est.ups.edu.ec.proyectoparqueo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "placas")
public class Placa implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, unique = true)
    private String placa;

    private String descripcion;

    @Column(name = "espacio_id")
    private Integer espacioId;

    private String estado;

    @Column(name = "fecha_inicio")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "America/Guayaquil")
    private LocalDateTime fechaInicio;

    @Column(precision = 10, scale = 2)
    private BigDecimal tarifa;

    @OneToMany(mappedBy = "placa", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Contrato> contratos = new ArrayList<>();

    @OneToMany(mappedBy = "placa", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Ticket> tickets = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getEspacioId() {
        return espacioId;
    }

    public void setEspacioId(Integer espacioId) {
        this.espacioId = espacioId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public BigDecimal getTarifa() {
        return tarifa;
    }

    public void setTarifa(BigDecimal tarifa) {
        this.tarifa = tarifa;
    }

    public List<Contrato> getContratos() {
        return contratos;
    }

    public void setContratos(List<Contrato> contratos) {
        this.contratos = contratos;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}