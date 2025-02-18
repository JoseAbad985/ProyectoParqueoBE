package est.ups.edu.ec.proyectoparqueo.service;

import est.ups.edu.ec.proyectoparqueo.repository.UserRepository;
import est.ups.edu.ec.proyectoparqueo.model.User;
import est.ups.edu.ec.proyectoparqueo.security.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordUtil passwordUtil;

    @Override
    public User findByEmail(String email) {
        LOGGER.info("Internal call to find user by email: " + email);
        if (email == null) return null;
        return userRepository.findByEmail(email.trim()).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByCedula(String cedula) {
        return userRepository.findById(cedula).orElse(null);
    }

    @Override
    public User createUser(User user) {
        if (!passwordUtil.isValidPassword(user.getPassword())) {  // Use instance method
            throw new IllegalArgumentException("La contraseña no cumple con los requisitos mínimos");
        }

        if (findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("El correo electrónico ya existe");
        }

        user.setPassword(passwordUtil.hashPassword(user.getPassword()));  // Use instance method
        return userRepository.save(user);
    }

    @Override
    public User updateUser(String cedula, User user) {
        User existingUser = userRepository.findById(cedula)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        existingUser.setNombre(user.getNombre());
        existingUser.setApellido(user.getApellido());
        existingUser.setRol(user.getRol());
        existingUser.setActivo(user.isActivo());

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(String cedula) {
        userRepository.deleteById(cedula);
    }
}