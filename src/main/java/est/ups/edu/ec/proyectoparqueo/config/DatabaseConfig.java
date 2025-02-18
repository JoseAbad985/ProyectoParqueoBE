package est.ups.edu.ec.proyectoparqueo.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "est.ups.edu.ec.proyectoparqueo.model")
@EnableJpaRepositories(basePackages = "est.ups.edu.ec.proyectoparqueo.repository")
public class DatabaseConfig {
    // Spring Boot auto-configures the EntityManager
}