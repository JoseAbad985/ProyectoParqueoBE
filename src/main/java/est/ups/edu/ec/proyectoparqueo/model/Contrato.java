package est.ups.edu.ec.proyectoparqueo.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "contratos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contrato implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placa_id", nullable = false)
    private Placa placa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_cedula", nullable = false)
    private User usuario;

    private String descripcion;
    private String estado;

    @Column(precision = 10, scale = 2)
    private BigDecimal tarifaContrato;

    @Column(name = "fecha_inicio")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaFin;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public Placa getPlaca() {
        return placa;
    }

    public void setPlaca(Placa placa) {
        this.placa = placa;
    }

    // Method to get placa string for JSON
    @JsonProperty("placa")
    public PlacaDTO getPlacaDTO() {
        if (placa != null) {
            return new PlacaDTO(placa.getPlaca());
        }
        return null;
    }

    // Method to set placa from JSON
    @JsonProperty("placa")
    public void setPlacaFromDTO(PlacaDTO placaDTO) {
        if (placaDTO != null) {
            Placa newPlaca = new Placa();
            newPlaca.setPlaca(placaDTO.getPlaca());
            this.placa = newPlaca;
        }
    }

    @JsonIgnore
    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    // Method to get user info for JSON
    @JsonProperty("usuario")
    public UserDTO getUsuarioDTO() {
        if (usuario != null) {
            return new UserDTO(usuario.getCedula());
        }
        return null;
    }

    // Method to set user from JSON
    @JsonProperty("usuario")
    public void setUsuarioFromDTO(UserDTO userDTO) {
        if (userDTO != null) {
            User newUser = new User();
            newUser.setCedula(userDTO.getCedula());
            this.usuario = newUser;
        }
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTarifaContrato() {
        return tarifaContrato;
    }

    public void setTarifaContrato(BigDecimal tarifaContrato) {
        this.tarifaContrato = tarifaContrato;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    // DTO classes for JSON serialization/deserialization
    public static class PlacaDTO {
        private String placa;

        public PlacaDTO() {}

        public PlacaDTO(String placa) {
            this.placa = placa;
        }

        public String getPlaca() {
            return placa;
        }

        public void setPlaca(String placa) {
            this.placa = placa;
        }
    }

    public static class UserDTO {
        private String cedula;

        public UserDTO() {}

        public UserDTO(String cedula) {
            this.cedula = cedula;
        }

        public String getCedula() {
            return cedula;
        }

        public void setCedula(String cedula) {
            this.cedula = cedula;
        }
    }
}