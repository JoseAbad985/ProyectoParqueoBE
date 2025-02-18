package est.ups.edu.ec.proyectoparqueo.security;

import est.ups.edu.ec.proyectoparqueo.repository.UserRepository;
import est.ups.edu.ec.proyectoparqueo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private final List<String> publicPaths = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/auth/google",
            "/auth/debug/create-admin",
            "/api/test/email"  // Added this line
    );

    public JwtFilter(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String path = request.getServletPath();
        logger.info("Processing request for path: {}", path);

        // Handle CORS preflight requests
        if (request.getMethod().equals("OPTIONS")) {
            logger.debug("Handling OPTIONS request");
            filterChain.doFilter(request, response);
            return;
        }

        // Skip authentication for public paths
        if (publicPaths.stream().anyMatch(path::startsWith)) {
            logger.debug("Skipping authentication for public path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        logger.debug("Authorization header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("No valid authorization header found");
            sendUnauthorizedError(response, "No valid authorization token found");
            return;
        }

        try {
            String token = authHeader.substring(7);
            String cedula = jwtUtil.extractCedula(token);
            logger.debug("Extracted cedula from token: {}", cedula);

            if (cedula != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = userRepository.findByCedula(cedula)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                logger.debug("Found user: {}", userDetails.getEmail());

                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Authentication successful for user: {}", cedula);

                    filterChain.doFilter(request, response);
                } else {
                    logger.warn("Invalid token for user: {}", cedula);
                    sendUnauthorizedError(response, "Invalid token");
                }
            } else {
                logger.warn("Could not set user authentication");
                sendUnauthorizedError(response, "Could not set user authentication");
            }
        } catch (Exception e) {
            logger.error("Authentication error: ", e);
            sendUnauthorizedError(response, "Authentication error: " + e.getMessage());
        }
    }

    private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}