package est.ups.edu.ec.proyectoparqueo.security;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, TYPE })
@Retention(RUNTIME)
@PreAuthorize("isAuthenticated()")
public @interface Secured {
}