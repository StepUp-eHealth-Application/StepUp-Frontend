package de.hskempten.stepupbackend.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepupbackend.dto.WeightObservationDTO;
import de.hskempten.stepupbackend.helpers.FhirHelpers;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ObservationController {

    @Autowired
    private PatientController patientController;

    public WeightObservationDTO addWeightObservation(WeightObservationDTO weightObservationDTO) {
        FhirContext ctx = FhirContext.forR4();
        String serverBase = weightObservationDTO.getFhirServer();

        IGenericClient client = ctx.newRestfulGenericClient(serverBase);

        Observation observation = new Observation();

        // Setting patient reference
        Patient patient = (Patient) patientController.searchPatientById(
            weightObservationDTO.getPatientID(),
            weightObservationDTO.getFhirServer(),
            client).getEntryFirstRep().getResource();

        Reference patientRef = new Reference(patient.getIdElement().getValue());
        patientRef.setType("Patient");
        observation.setSubject(patientRef);

        // Setting concept
        observation
            .getCode()
                .addCoding()
                    .setSystem("http://loinc.org")
                    .setCode("29463-7")
                    .setDisplay("Body weight measured at " + weightObservationDTO.getDate().toString());

        // Setting value
        // TODO: add date when possible
        observation.setValue(
            new Quantity()
                .setValue(weightObservationDTO.getWeight())
                .setUnit("kg")
                .setSystem("http://unitsofmeasure.org")
        );

        Observation createObservation = (Observation) client.create().resource(observation).execute().getResource();
        FhirHelpers.PrettyPrint(createObservation, ctx);

        weightObservationDTO.setId(createObservation.getIdElement().getIdPart());

        return weightObservationDTO;
    }
}
