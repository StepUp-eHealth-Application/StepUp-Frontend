package de.hskempten.stepupbackend.endpoints.v1;

import de.hskempten.stepupbackend.controllers.SettingsController;
import de.hskempten.stepupbackend.dto.SettingsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settings/")
public class SettingsEndpoint {

    @Autowired
    private SettingsController settingsController;

    @GetMapping
    public HttpEntity<SettingsDTO> getSettings() {
        SettingsDTO settings = settingsController.getAllSettings();
        return new ResponseEntity<>(settings, HttpStatus.OK);
    }

    @PostMapping("fhirserver")
    public HttpEntity<SettingsDTO> setFhirServerUrl(@RequestBody SettingsDTO settingsDTO) {
        settingsDTO = settingsController.setFhirServerUrl(settingsDTO);
        return new ResponseEntity<>(settingsDTO, HttpStatus.CREATED);
    }
}
