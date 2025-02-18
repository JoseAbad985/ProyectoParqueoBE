package est.ups.edu.ec.proyectoparqueo.service;

import est.ups.edu.ec.proyectoparqueo.model.ParqueoConfiguraciones;
import est.ups.edu.ec.proyectoparqueo.repository.ParqueoConfiguracionesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

@Service
public class ParqueoConfiguracionesService {
    private static final Logger logger = LoggerFactory.getLogger(ParqueoConfiguracionesService.class);
    private final ParqueoConfiguracionesRepository repository;
    private static final List<DateTimeFormatter> TIME_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("hh:mm a"),
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("h:mm a"),
            DateTimeFormatter.ofPattern("H:mm")
    );

    @Autowired
    public ParqueoConfiguracionesService(ParqueoConfiguracionesRepository repository) {
        this.repository = repository;
    }

    public boolean isOneHourBeforeClosingTime() {
        try {
            ParqueoConfiguraciones config = getOrCreateConfiguracion();
            String horarioCierre = config.getHorarioCierre();
            logger.debug("Closing time from config: {}", horarioCierre);

            LocalTime closingTime = parseTime(horarioCierre);
            if (closingTime == null) {
                logger.error("Could not parse closing time with any known format");
                return false;
            }

            LocalTime currentTime = LocalTime.now();
            LocalTime oneHourBeforeClosing = closingTime.minusHours(1);

            logger.debug("Current time: {}", currentTime);
            logger.debug("One hour before closing: {}", oneHourBeforeClosing);
            logger.debug("Closing time: {}", closingTime);

            boolean isTimeToNotify = currentTime.isAfter(oneHourBeforeClosing)
                    && currentTime.isBefore(closingTime);

            logger.debug("Is time to notify? {}", isTimeToNotify);
            return isTimeToNotify;

        } catch (Exception e) {
            logger.error("Unexpected error checking closing time: {}", e.getMessage());
            return false;
        }
    }

    private LocalTime parseTime(String timeStr) {
        for (DateTimeFormatter formatter : TIME_FORMATTERS) {
            try {
                return LocalTime.parse(timeStr, formatter);
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }
        return null;
    }

    public ParqueoConfiguraciones getOrCreateConfiguracion() {
        ParqueoConfiguraciones config = repository.findConfiguracion().orElse(null);

        if (config == null) {
            config = new ParqueoConfiguraciones();
            config.setNombreParqueadero("Parqueadero");
            config.setCapacidadMaxima(10);
            config.setTarifaPorHora(BigDecimal.valueOf(1.0));
            config.setTarifaContrato(BigDecimal.valueOf(15.0));
            config.setHorarioApertura("08:00");
            config.setHorarioCierre("20:00");
            return repository.save(config);
        }

        return config;
    }

    public ParqueoConfiguraciones updateConfiguracion(ParqueoConfiguraciones updatedConfig) {
        logger.info("Updating configuration: {}", updatedConfig);
        ParqueoConfiguraciones existingConfig = getOrCreateConfiguracion();

        existingConfig.setNombreParqueadero(updatedConfig.getNombreParqueadero());
        existingConfig.setCapacidadMaxima(updatedConfig.getCapacidadMaxima());
        existingConfig.setTarifaPorHora(BigDecimal.valueOf(updatedConfig.getTarifaPorHora()));
        existingConfig.setTarifaContrato(BigDecimal.valueOf(updatedConfig.getTarifaContrato()));
        existingConfig.setHorarioApertura(updatedConfig.getHorarioApertura());
        existingConfig.setHorarioCierre(updatedConfig.getHorarioCierre());

        ParqueoConfiguraciones savedConfig = repository.save(existingConfig);
        logger.info("Configuration updated successfully: {}", savedConfig);
        return savedConfig;
    }
}