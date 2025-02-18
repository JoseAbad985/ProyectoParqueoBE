package est.ups.edu.ec.proyectoparqueo.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import est.ups.edu.ec.proyectoparqueo.repository.UserRepository;
import est.ups.edu.ec.proyectoparqueo.model.User;
import est.ups.edu.ec.proyectoparqueo.model.UserRole;
import est.ups.edu.ec.proyectoparqueo.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Value("${google.client.id}")
    private String googleClientId;

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(
            UserRepository userRepository,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, Object> loginWithGoogle(String credential) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(credential);
            if (idToken == null) {
                throw new RuntimeException("Invalid Google token");
            }

            Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            // Check if user exists
            Optional<User> existingUser = userRepository.findByEmail(email);
            User user;

            if (existingUser.isEmpty()) {
                // Create new user with Google data
                user = new User();
                user.setEmail(email);
                user.setNombre((String) payload.get("given_name"));
                user.setApellido((String) payload.get("family_name"));
                // Generate a 9-digit number and prefix with 'G'
                String googleId = String.format("G%09d", Math.abs(email.hashCode()) % 1000000000);
                user.setCedula(googleId);
                user.setPassword(passwordEncoder.encode("GOOGLE_AUTH_" + System.currentTimeMillis()));
                user.setRol(UserRole.CLIENTE);
                user.setActivo(true);

                userRepository.save(user);
            } else {
                user = existingUser.get();
                if (!user.isActivo()) {
                    throw new RuntimeException("Usuario inactivo");
                }
            }

            String token = jwtUtil.generateToken(user);

            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("cedula", user.getCedula());
            userDetails.put("nombre", user.getNombre());
            userDetails.put("apellido", user.getApellido());
            userDetails.put("email", user.getEmail());
            userDetails.put("rol", user.getRol());

            return Map.of(
                    "token", token,
                    "user", userDetails
            );

        } catch (Exception e) {
            logger.error("Error during Google authentication", e);
            throw new RuntimeException("Error al autenticar con Google: " + e.getMessage());
        }
    }

    public Map<String, Object> login(String email, String password) {
        logger.info("Login attempt for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new RuntimeException("Usuario no encontrado");
                });

        logger.info("User found: {}", user.getEmail());
        logger.info("Stored password: {}", user.getPassword());
        logger.info("Provided password: {}", password);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.error("Invalid password for user: {}", email);
            throw new RuntimeException("Contraseña incorrecta");
        }

        if (!user.isActivo()) {
            logger.error("Inactive user login attempt: {}", email);
            throw new RuntimeException("Usuario inactivo");
        }

        String token = jwtUtil.generateToken(user);

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("cedula", user.getCedula());
        userDetails.put("nombre", user.getNombre());
        userDetails.put("apellido", user.getApellido());
        userDetails.put("email", user.getEmail());
        userDetails.put("rol", user.getRol());

        logger.info("Login successful for user: {}", email);
        return Map.of(
                "token", token,
                "user", userDetails
        );
    }

    public void register(
            String cedula,
            String nombre,
            String apellido,
            String email,
            String password,
            String rolString
    ) {
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }

        if (userRepository.findByCedula(cedula).isPresent()) {
            throw new IllegalArgumentException("La cédula ya está registrada");
        }

        // Create new user
        User newUser = new User();
        newUser.setCedula(cedula);
        newUser.setNombre(nombre);
        newUser.setApellido(apellido);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRol(UserRole.valueOf(rolString.toUpperCase()));
        newUser.setActivo(true);

        userRepository.save(newUser);
    }

    public boolean validateToken(String token) {
        try {
            String cedula = jwtUtil.extractCedula(token);
            User user = userRepository.findByCedula(cedula)
                    .orElseThrow(() -> new RuntimeException("Invalid token"));

            return jwtUtil.validateToken(token, user);
        } catch (Exception e) {
            return false;
        }
    }
}