package est.ups.edu.ec.proyectoparqueo.controller;

import est.ups.edu.ec.proyectoparqueo.model.ParqueoConfiguraciones;
import est.ups.edu.ec.proyectoparqueo.service.ParqueoConfiguracionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/configuraciones")
@CrossOrigin(origins = "http://localhost:4200")
public class ParqueoConfiguracionesController {

    private final ParqueoConfiguracionesService service;

    @Autowired
    public ParqueoConfiguracionesController(ParqueoConfiguracionesService service) {
        this.service = service;
    }

    @GetMapping("/tarifas")
    public ResponseEntity<Map<String, Double>> getTarifas() {
        try {
            ParqueoConfiguraciones config = service.getOrCreateConfiguracion();
            Map<String, Double> tarifas = new HashMap<>();
            tarifas.put("tarifaPorHora", config.getTarifaPorHora());
            tarifas.put("tarifaContrato", config.getTarifaContrato());
            return ResponseEntity.ok(tarifas);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParqueoConfiguraciones> getConfiguracion() {
        try {
            ParqueoConfiguraciones config = service.getOrCreateConfiguracion();
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParqueoConfiguraciones> updateConfiguracion(@RequestBody ParqueoConfiguraciones config) {
        try {
            ParqueoConfiguraciones updatedConfig = service.updateConfiguracion(config);
            return ResponseEntity.ok(updatedConfig);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}