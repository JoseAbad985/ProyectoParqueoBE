package est.ups.edu.ec.proyectoparqueo.security;

import est.ups.edu.ec.proyectoparqueo.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private static final String SECRET = "your-256-bit-secret-your-256-bit-secret-your-256-bit-secret";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("cedula", user.getCedula());
        claims.put("rol", "ROLE_" + user.getRol());
        claims.put("nombre", user.getNombre());
        claims.put("apellido", user.getApellido());
        claims.put("email", user.getEmail());

        // Add authorities explicitly
        claims.put("authorities", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getCedula())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String cedula = extractCedula(token);
            return (cedula.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    public String extractCedula(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}