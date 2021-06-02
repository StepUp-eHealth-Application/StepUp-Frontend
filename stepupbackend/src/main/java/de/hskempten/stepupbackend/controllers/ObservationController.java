package de.hskempten.stepupbackend.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepupbackend.dto.WeightObservationDTO;
import de.hskempten.stepupbackend.helpers.FhirHelpers;
import org.apache.tomcat.jni.Local;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public List<WeightObservationDTO> getWeightsByPatientId(String id) {
        String fhirServer = "http://hapi.fhir.org/baseR4/"; // TODO: get fhir server from database

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        // Getting patient
        Patient patient = (Patient) patientController.searchPatientById(
            id,
            fhirServer,
            client).getEntryFirstRep().getResource();

        Bundle observations = client.search()
            .forResource(Observation.class)
            .where(Observation.SUBJECT.hasId(patient.getIdElement().getValue()))
            .returnBundle(Bundle.class)
            .execute();

        List<WeightObservationDTO> retObervations = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : observations.getEntry()) {
            Observation observation = (Observation) entry.getResource();

            WeightObservationDTO weightObservationDTO = new WeightObservationDTO();
            weightObservationDTO.setId(observation.getIdElement().getIdPart());
            weightObservationDTO.setPatientID(patient.getIdElement().getIdPart());
            weightObservationDTO.setFhirServer(fhirServer);

            try {
                String dateStr = observation.getCode().getCoding().get(0).getDisplay().replace("Body weight measured at ", "");
                System.out.println("Date String: " + dateStr);
                LocalDate date = LocalDate.parse(dateStr);
                weightObservationDTO.setDate(date);
            } catch (DateTimeParseException exception) {
                System.out.println("Could not parse date time");
                weightObservationDTO.setDate(null);
            }

            retObervations.add(weightObservationDTO);
        }

        return retObervations;
    }
}
