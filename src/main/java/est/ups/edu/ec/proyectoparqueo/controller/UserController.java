package est.ups.edu.ec.proyectoparqueo.controller;

import est.ups.edu.ec.proyectoparqueo.service.UserService;
import est.ups.edu.ec.proyectoparqueo.model.User;
import est.ups.edu.ec.proyectoparqueo.security.Secured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/users")
@Secured
public class UserController {
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserService userService;

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            LOGGER.info("Searching for user with email: " + email);
            User user = userService.findByEmail(email);

            if (user != null) {
                LOGGER.info("User found: " + user.getNombre() + " " + user.getApellido());
                return ResponseEntity.ok(user);
            }

            LOGGER.warning("No user found with email: " + email);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            LOGGER.severe("Error finding user by email: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error processing request");
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{cedula}")
    public ResponseEntity<?> getUserByCedula(@PathVariable String cedula) {
        User user = userService.getUserByCedula(cedula);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{cedula}")
    public ResponseEntity<?> updateUser(@PathVariable String cedula, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(cedula, user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating user");
        }
    }

    @DeleteMapping("/{cedula}")
    public ResponseEntity<?> deleteUser(@PathVariable String cedula) {
        try {
            userService.deleteUser(cedula);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting user");
        }
    }
}