package est.ups.edu.ec.proyectoparqueo.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class TestEntity implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}