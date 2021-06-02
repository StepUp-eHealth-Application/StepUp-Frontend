package de.hskempten.stepupbackend.controllers;

import de.hskempten.stepupbackend.dto.SettingsDTO;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


@Controller
public class SettingsController {

    private static final String FHIR_SETTINGS_FILE = "fhir_settings.dat";

    private String fhirUrl = "";

    public SettingsDTO setFhirServerUrl(SettingsDTO settingsDTO) {
        FileWriter settingsFile = null;
        try {
            settingsFile = new FileWriter(FHIR_SETTINGS_FILE);
            settingsFile.write(settingsDTO.getFhirUrl());
            settingsFile.close();

            fhirUrl = settingsDTO.getFhirUrl();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SettingsDTO settings = getAllSettings();
        return settings;
    }

    public SettingsDTO getAllSettings() {
        SettingsDTO settings = new SettingsDTO();

        // Reading FHIR URL
        File file = new File(FHIR_SETTINGS_FILE);
        try {
            Scanner scanner = new Scanner(file);
            String url = scanner.nextLine();
            fhirUrl = url;
            settings.setFhirUrl(url);
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return settings;
    }

    public String getFhirServerUrl() {
        if (fhirUrl != null && fhirUrl.length() > 0) {
            return fhirUrl;
        }

        return getAllSettings().getFhirUrl();
    }
}
