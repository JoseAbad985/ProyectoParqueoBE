package est.ups.edu.ec.proyectoparqueo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "parqueo_configuraciones")
public class ParqueoConfiguraciones implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del parqueadero es requerido")
    private String nombreParqueadero;

    @Min(value = 1, message = "La capacidad máxima debe ser mayor a 0")
    private int capacidadMaxima;

    @Column(precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "La tarifa por hora debe ser mayor o igual a 0")
    private BigDecimal tarifaPorHora;

    @Column(precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "La tarifa del contrato debe ser mayor o igual a 0")
    private BigDecimal tarifaContrato;

    @NotBlank(message = "El horario de apertura es requerido")
    private String horarioApertura;

    @NotBlank(message = "El horario de cierre es requerido")
    private String horarioCierre;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreParqueadero() { return nombreParqueadero; }
    public void setNombreParqueadero(String nombreParqueadero) { this.nombreParqueadero = nombreParqueadero; }

    public int getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(int capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }

    public double getTarifaPorHora() {
        return tarifaPorHora != null ? tarifaPorHora.doubleValue() : 0.0;
    }

    public void setTarifaPorHora(BigDecimal tarifaPorHora) { this.tarifaPorHora = tarifaPorHora; }

    public double getTarifaContrato() {
        return tarifaContrato != null ? tarifaContrato.doubleValue() : 0.0;
    }

    public void setTarifaContrato(BigDecimal tarifaContrato) { this.tarifaContrato = tarifaContrato; }

    public String getHorarioApertura() { return horarioApertura; }
    public void setHorarioApertura(String horarioApertura) { this.horarioApertura = horarioApertura; }

    public String getHorarioCierre() { return horarioCierre; }
    public void setHorarioCierre(String horarioCierre) { this.horarioCierre = horarioCierre; }
}