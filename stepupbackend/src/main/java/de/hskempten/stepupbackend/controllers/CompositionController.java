package de.hskempten.stepupbackend.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepupbackend.dto.CompositionDTO;
import de.hskempten.stepupbackend.fhir.FhirClient;
import de.hskempten.stepupbackend.helpers.FhirHelpers;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CompositionController {

    @Autowired
    PatientController patientController;

    @Autowired
    SettingsController settingsController;

    public CompositionDTO createComposition(CompositionDTO compositionDTO) {
        Device fhirDevice = new Device();

        String fhirServer = settingsController.getFhirServerUrl();
        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Bundle patientBundle = patientController.searchPatientById(compositionDTO.getPatientId(), fhirServer, client);
        if (patientBundle == null || patientBundle.getEntryFirstRep() == null) {
            return null;
        }

        Patient patient = (Patient) patientBundle.getEntryFirstRep().getResource();

        Composition composition = new Composition();

        Reference patientReference = new Reference(patient);
        patientReference.setType("Patient");
        composition.setSubject(patientReference);

        

        return null;
    }
}
