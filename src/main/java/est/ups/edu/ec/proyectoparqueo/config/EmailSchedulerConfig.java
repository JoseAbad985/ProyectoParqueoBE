package est.ups.edu.ec.proyectoparqueo.config;

import est.ups.edu.ec.proyectoparqueo.service.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class EmailSchedulerConfig {

    @Autowired
    private EmailNotificationService emailNotificationService;

    @Scheduled(cron = "0 * * * * *")
    public void scheduleClosingNotifications() {
        emailNotificationService.sendClosingNotificationEmails();
    }
}