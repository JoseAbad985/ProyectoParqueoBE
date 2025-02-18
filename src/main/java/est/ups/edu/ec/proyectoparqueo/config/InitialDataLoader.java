package est.ups.edu.ec.proyectoparqueo.config;

import est.ups.edu.ec.proyectoparqueo.model.User;
import est.ups.edu.ec.proyectoparqueo.model.UserRole;
import est.ups.edu.ec.proyectoparqueo.repository.UserRepository;
import est.ups.edu.ec.proyectoparqueo.security.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class InitialDataLoader {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordUtil passwordUtil;  // Add this

    @Bean
    public CommandLineRunner initializeAdminUser() {
        return args -> {
            if (userRepository.findByEmail("admin@system.com").isEmpty()) {
                User adminUser = new User(
                        "1234567890",
                        "Admin",
                        "User",
                        "admin@system.com",
                        passwordUtil.hashPassword("AdminPassword123!"),  // Use instance method
                        UserRole.ADMIN
                );
                userRepository.save(adminUser);
                System.out.println("Initial admin user created");
            }
        };
    }
}