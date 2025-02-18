package est.ups.edu.ec.proyectoparqueo.service;

import est.ups.edu.ec.proyectoparqueo.model.User;
import java.util.List;

public interface UserService {
    User findByEmail(String email);
    List<User> getAllUsers();
    User getUserByCedula(String cedula);
    User createUser(User user);
    User updateUser(String cedula, User user);
    void deleteUser(String cedula);
}