package est.ups.edu.ec.proyectoparqueo.config;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(sendGridApiKey);
    }
}