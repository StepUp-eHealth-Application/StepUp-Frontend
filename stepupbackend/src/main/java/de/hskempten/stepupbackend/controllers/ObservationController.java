package de.hskempten.stepupbackend.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepupbackend.dto.StepsObservationDTO;
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

    @Autowired
    private SettingsController settingsController;

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
        String fhirServer = settingsController.getFhirServerUrl();

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

            // Skipping step observations
            if (!observation.getCode().getCoding().get(0).getDisplay().contains("Body weight measured at")) {
                continue;
            }

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

            // Setting value
            Quantity value = (Quantity) observation.getValue();
            float steps = value.getValue().floatValue();
            weightObservationDTO.setWeight(steps);

            retObervations.add(weightObservationDTO);
        }

        return retObervations;
    }

    public StepsObservationDTO addStepsObservation(StepsObservationDTO stepsObservationDTO) {
        FhirContext ctx = FhirContext.forR4();
        String serverBase = stepsObservationDTO.getFhirServer();

        IGenericClient client = ctx.newRestfulGenericClient(serverBase);

        Observation observation = new Observation();

        // Setting patient reference
        Patient patient = (Patient) patientController.searchPatientById(
            stepsObservationDTO.getPatientID(),
            stepsObservationDTO.getFhirServer(),
            client).getEntryFirstRep().getResource();

        Reference patientRef = new Reference(patient.getIdElement().getValue());
        patientRef.setType("Patient");
        observation.setSubject(patientRef);

        // Setting concept
        observation
            .getCode()
            .addCoding()
            .setSystem("http://loinc.org")
            .setCode("41950-7")
            .setDisplay("Number of Steps in 24 Hours, Measured at " + stepsObservationDTO.getDate().toString());

        // Setting value
        // TODO: add date when possible
        observation.setValue(
            new Quantity()
                .setValue(stepsObservationDTO.getSteps())
                .setUnit("Number of Steps in 24 Hours")
        );

        Observation createObservation = (Observation) client.create().resource(observation).execute().getResource();
        FhirHelpers.PrettyPrint(createObservation, ctx);

        stepsObservationDTO.setId(createObservation.getIdElement().getIdPart());

        return stepsObservationDTO;
    }

    public List<StepsObservationDTO> getStepsByPatientId(String id) {
        String fhirServer = settingsController.getFhirServerUrl();

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

        List<StepsObservationDTO> retObervations = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : observations.getEntry()) {
            Observation observation = (Observation) entry.getResource();

            // Skipping weight observations
            if (!observation.getCode().getCoding().get(0).getDisplay().contains("Number of Steps in 24 Hours, Measured at ")) {
                continue;
            }

            StepsObservationDTO stepsObservationDto = new StepsObservationDTO();
            stepsObservationDto.setId(observation.getIdElement().getIdPart());
            stepsObservationDto.setPatientID(patient.getIdElement().getIdPart());
            stepsObservationDto.setFhirServer(fhirServer);

            try {
                String dateStr = observation.getCode().getCoding().get(0).getDisplay().replace("Number of Steps in 24 Hours, Measured at ", "");
                System.out.println("Date String: " + dateStr);
                LocalDate date = LocalDate.parse(dateStr);
                stepsObservationDto.setDate(date);
            } catch (DateTimeParseException exception) {
                System.out.println("Could not parse date time");
                stepsObservationDto.setDate(null);
            }

            // Setting value
            Quantity value = (Quantity) observation.getValue();
            int steps = value.getValue().intValue();
            stepsObservationDto.setSteps(steps);

            retObervations.add(stepsObservationDto);
        }

        return retObervations;
    }
}
