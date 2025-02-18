package est.ups.edu.ec.proyectoparqueo.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class PasswordUtil {
    private static final int MIN_LENGTH = 8;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public PasswordUtil(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean verifyPassword(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }

    public boolean isValidPassword(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return false;
        }

        return UPPERCASE_PATTERN.matcher(password).find() &&
                LOWERCASE_PATTERN.matcher(password).find() &&
                NUMBER_PATTERN.matcher(password).find() &&
                SPECIAL_CHAR_PATTERN.matcher(password).find();
    }
}