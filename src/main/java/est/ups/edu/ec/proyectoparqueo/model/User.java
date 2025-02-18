package est.ups.edu.ec.proyectoparqueo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @Column(unique = true, length = 10)
    @NotBlank(message = "La cédula es requerida")
    @Pattern(regexp = "^([0-9]{10}|G[0-9]{9})$", message = "La cédula debe tener 10 dígitos o comenzar con G seguido de 9 dígitos")
    private String cedula;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @Column(nullable = false)
    @NotBlank(message = "El apellido es requerido")
    private String apellido;

    @Column(nullable = false, unique = true)
    @Email(message = "El correo electrónico debe ser válido")
    @NotBlank(message = "El correo electrónico es requerido")
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole rol;

    @Column(nullable = false)
    private Boolean activo = true;

    public User() {}

    public User(String cedula, String nombre, String apellido, String email, String password, UserRole rol) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRol() {
        return rol;
    }

    public void setRol(UserRole rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getUsername() {
        return cedula;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}