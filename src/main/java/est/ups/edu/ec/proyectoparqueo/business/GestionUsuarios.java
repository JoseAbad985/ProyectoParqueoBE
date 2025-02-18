package est.ups.edu.ec.proyectoparqueo.business;

import est.ups.edu.ec.proyectoparqueo.repository.UserRepository;
import est.ups.edu.ec.proyectoparqueo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GestionUsuarios {

    @Autowired
    private UserRepository userRepository;

    /**
     * Crea un nuevo usuario.
     *
     * @param user Objeto User a crear.
     * @throws Exception Si hay errores en la validación o persistencia.
     */
    public void crearUsuario(User user) throws Exception {
        if (user.getCedula() == null || user.getCedula().length() != 10) {
            throw new Exception("La cédula debe tener exactamente 10 caracteres.");
        }

        if (userRepository.findById(user.getCedula()).isPresent()) {
            throw new Exception("El usuario con cédula " + user.getCedula() + " ya existe.");
        }

        userRepository.save(user);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param cedula Cédula del usuario a actualizar.
     * @param user   Objeto User con los nuevos datos.
     * @throws Exception Si el usuario no existe o hay errores en la validación.
     */
    public void actualizarUsuario(String cedula, User user) throws Exception {
        User existente = userRepository.findById(cedula)
                .orElseThrow(() -> new Exception("Usuario no encontrado con la cédula: " + cedula));

        // Actualizar datos del usuario
        existente.setNombre(user.getNombre());
        existente.setEmail(user.getEmail());
        // Otros atributos según el modelo

        userRepository.save(existente);
    }

    /**
     * Lista todos los usuarios.
     *
     * @return Lista de usuarios.
     */
    public List<User> listarUsuarios() {
        return userRepository.findAll();
    }

    /**
     * Elimina un usuario existente.
     *
     * @param cedula Cédula del usuario a eliminar.
     * @throws Exception Si el usuario no existe.
     */
    public void eliminarUsuario(String cedula) throws Exception {
        if (!userRepository.existsById(cedula)) {
            throw new Exception("Usuario no encontrado con la cédula: " + cedula);
        }

        userRepository.deleteById(cedula);
    }
}