package est.ups.edu.ec.proyectoparqueo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@EnableWebMvc
@OpenAPIDefinition(
        info = @Info(
                title = "Parqueo API",
                version = "1.0",
                description = "API para gestión de parqueadero"
        )
)
public class RestApplication {}