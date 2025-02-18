package est.ups.edu.ec.proyectoparqueo.controller;

import est.ups.edu.ec.proyectoparqueo.model.GoogleAuthRequest;
import est.ups.edu.ec.proyectoparqueo.model.User;
import est.ups.edu.ec.proyectoparqueo.model.UserRole;
import est.ups.edu.ec.proyectoparqueo.repository.UserRepository;
import est.ups.edu.ec.proyectoparqueo.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {
        "http://localhost:4200",
        "https://proyectoparqueo-9b413.web.app",
        "https://proyectoparqueo-9b413.firebaseapp.com"
}, allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS},
        allowCredentials = "true")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleAuthRequest request) {
        logger.info("Google login request received");

        try {
            Map<String, Object> response = authService.loginWithGoogle(request.getCredential());
            logger.info("Google login successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Google login failed", e);
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        logger.info("Login request received");

        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            logger.info("Login attempt for email: {}", email);

            Map<String, Object> response = authService.login(email, password);

            logger.info("Login successful for email: {}", email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login failed", e);
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userData) {
        try {
            authService.register(
                    userData.get("cedula"),
                    userData.get("nombre"),
                    userData.get("apellido"),
                    userData.get("email"),
                    userData.get("password"),
                    "CLIENTE"
            );
            return ResponseEntity.status(201).body(Map.of("message", "User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/debug/check-user")
    public ResponseEntity<?> checkUser(@RequestParam String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "exists", true,
                    "email", user.get().getEmail(),
                    "password", "REDACTED"
            ));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/debug/create-admin")
    public ResponseEntity<?> createDebugAdmin() {
        try {
            User adminUser = new User();
            adminUser.setCedula("0000000001");
            adminUser.setNombre("Admin");
            adminUser.setApellido("System");
            adminUser.setEmail("admin@system.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRol(UserRole.ADMIN);
            adminUser.setActivo(true);

            userRepository.save(adminUser);

            return ResponseEntity.ok("Debug admin user created");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating debug admin: " + e.getMessage());
        }
    }
}