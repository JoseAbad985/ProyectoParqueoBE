package est.ups.edu.ec.proyectoparqueo.controller;

import est.ups.edu.ec.proyectoparqueo.service.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private EmailNotificationService emailNotificationService;

    @PostMapping("/email")
    public ResponseEntity<String> testEmail(@RequestParam String email) {
        try {
            emailNotificationService.sendTestEmail(email);
            return ResponseEntity.ok("Test email sent to: " + email);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error sending test email: " + e.getMessage());
        }
    }
}