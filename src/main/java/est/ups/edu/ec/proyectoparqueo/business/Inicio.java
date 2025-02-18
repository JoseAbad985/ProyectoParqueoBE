package est.ups.edu.ec.proyectoparqueo.business;

import est.ups.edu.ec.proyectoparqueo.repository.UserRepository;
import est.ups.edu.ec.proyectoparqueo.model.User;
import est.ups.edu.ec.proyectoparqueo.model.UserRole;
import est.ups.edu.ec.proyectoparqueo.security.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Inicio implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordUtil passwordUtil;


    @Override
    @Transactional
    public void run(String... args) {
        try {
            long adminCount = userRepository.countActiveAdmins(UserRole.ADMIN);

            if (adminCount == 0) {
                User adminUser = new User();
                adminUser.setCedula("0123456789");
                adminUser.setNombre("Admin");
                adminUser.setApellido("System");
                adminUser.setEmail("admin@system.com");
                adminUser.setPassword(passwordUtil.hashPassword("Admin123!")); // Use instance method
                adminUser.setRol(UserRole.ADMIN);
                adminUser.setActivo(true);
                userRepository.save(adminUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}