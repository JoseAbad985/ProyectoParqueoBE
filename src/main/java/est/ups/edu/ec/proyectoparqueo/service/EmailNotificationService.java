package est.ups.edu.ec.proyectoparqueo.service;

import com.sendgrid.SendGrid;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import est.ups.edu.ec.proyectoparqueo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.io.IOException;

@Service
public class EmailNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);
    private static final String SENDER_EMAIL = "joseabad.9b@hotmail.com";
    private static final String SENDER_NAME = "Sistema de Parqueadero";

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    private final ParqueoConfiguracionesService parqueoConfiguracionesService;
    private final UserRepository userRepository;
    private boolean notificationSent = false;

    @Autowired
    public EmailNotificationService(ParqueoConfiguracionesService parqueoConfiguracionesService,
                                    UserRepository userRepository) {
        this.parqueoConfiguracionesService = parqueoConfiguracionesService;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 300000) // Checks every 5 minutes
    public void sendClosingNotificationEmails() {
        logger.debug("Checking if it's time to send closing notifications...");

        if (parqueoConfiguracionesService.isOneHourBeforeClosingTime()) {
            if (notificationSent) {
                logger.debug("Notifications already sent for this closing time");
                return;
            }

            List<String> userEmails = userRepository.findAllActiveUserEmails();
            logger.info("Found {} active users to notify", userEmails.size());

            if (userEmails.isEmpty()) {
                logger.info("No active users to notify");
                return;
            }

            try {
                SendGrid sg = new SendGrid(sendGridApiKey);
                Email from = new Email(SENDER_EMAIL, SENDER_NAME);
                String subject = "Notificación de cierre del parqueadero";
                String content = "El parqueadero cerrará en 1 hora. Por favor, retire su vehículo a tiempo.";

                boolean allSuccessful = true;

                for (String emailTo : userEmails) {
                    Email to = new Email(emailTo);
                    Content mailContent = new Content("text/plain", content);
                    Mail mail = new Mail(from, subject, to, mailContent);

                    Request request = new Request();
                    request.setMethod(Method.POST);
                    request.setEndpoint("mail/send");
                    request.setBody(mail.build());

                    Response response = sg.api(request);
                    if (response.getStatusCode() != 202) {
                        logger.error("Failed to send email to {}: Status Code {}", emailTo, response.getStatusCode());
                        logger.error("Response body: {}", response.getBody());
                        allSuccessful = false;
                    } else {
                        logger.info("Successfully sent notification to {}", emailTo);
                    }
                }

                if (allSuccessful) {
                    notificationSent = true;
                    logger.info("Successfully sent closing notifications to all users");
                }
            } catch (IOException e) {
                logger.error("Failed to send closing notifications: {}", e.getMessage(), e);
            }
        } else {
            // Reset the notification flag when we're not in the notification window
            notificationSent = false;
            logger.debug("Not time to send closing notifications yet");
        }
    }

    // Test method - you can call this to test email sending
    public void sendTestEmail(String testEmail) {
        try {
            SendGrid sg = new SendGrid(sendGridApiKey);
            Email from = new Email(SENDER_EMAIL, SENDER_NAME);
            Email to = new Email(testEmail);
            String subject = "Test - Sistema de Parqueadero";
            Content mailContent = new Content("text/plain", "Esta es una prueba del sistema de notificaciones del parqueadero.");

            Mail mail = new Mail(from, subject, to, mailContent);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            logger.info("Test email response code: {}", response.getStatusCode());
            if (response.getStatusCode() != 202) {
                logger.error("Test email failed. Response body: {}", response.getBody());
            } else {
                logger.info("Test email sent successfully");
            }
        } catch (IOException e) {
            logger.error("Failed to send test email: {}", e.getMessage(), e);
        }
    }
}