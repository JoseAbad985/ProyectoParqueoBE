package est.ups.edu.ec.proyectoparqueo.security;

import est.ups.edu.ec.proyectoparqueo.model.User;
import est.ups.edu.ec.proyectoparqueo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String cedula) throws UsernameNotFoundException {
        User user = userRepository.findByCedula(cedula)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con cédula: " + cedula));

        return new org.springframework.security.core.userdetails.User(
                user.getCedula(),
                user.getPassword(),
                user.isActivo(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRol().name()))
        );
    }
}