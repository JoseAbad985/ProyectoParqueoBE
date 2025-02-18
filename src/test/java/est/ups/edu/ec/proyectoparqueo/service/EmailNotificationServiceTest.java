package est.ups.edu.ec.proyectoparqueo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import est.ups.edu.ec.proyectoparqueo.repository.UserRepository;

@SpringBootTest
class EmailNotificationServiceTest {

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ParqueoConfiguracionesService parqueoConfiguracionesService;

    @Autowired
    private EmailNotificationService emailNotificationService;

    @Test
    void testBasicFunctionality() {
        // Basic test to verify context loads
        assert(emailNotificationService != null);
    }
}