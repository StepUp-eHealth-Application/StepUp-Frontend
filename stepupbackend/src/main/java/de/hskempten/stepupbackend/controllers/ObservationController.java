package de.hskempten.stepupbackend.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepupbackend.dto.DateDTO;
import de.hskempten.stepupbackend.dto.StepsGoalDTO;
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

    @Autowired
    private DeviceController deviceController;

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

        Reference patientRef = new Reference(patient);
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

            WeightObservationDTO weightObservationDTO = convertObservationToWeightObservation(fhirServer, patient, observation);

            retObervations.add(weightObservationDTO);
        }

        return retObervations;
    }

    private WeightObservationDTO convertObservationToWeightObservation(String fhirServer, Patient patient, Observation observation) {
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
        return weightObservationDTO;
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

        Reference patientRef = new Reference(patient);
        patientRef.setType("Patient");
        observation.setSubject(patientRef);

        // Setting device reference
        Device device = (Device) deviceController
            .searchDeviceById(stepsObservationDTO.getDeviceID(), stepsObservationDTO.getFhirServer(), client)
            .getEntryFirstRep()
            .getResource();

        Reference deviceRef = new Reference(device);
        deviceRef.setType("Device");
        observation.setDevice(deviceRef);

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

            StepsObservationDTO stepsObservationDto = convertObservationToStepsDto(fhirServer, patient, observation);

            retObervations.add(stepsObservationDto);
        }

        return retObervations;
    }

    private StepsObservationDTO convertObservationToStepsDto(String fhirServer, Patient patient, Observation observation) {
        StepsObservationDTO stepsObservationDto = new StepsObservationDTO();
        stepsObservationDto.setId(observation.getIdElement().getIdPart());

        String ref = observation.getSubject().getReference();
        if (ref.contains("/")) {
            var refParts = ref.split("/");
            ref = refParts[refParts.length - 1];
        }
        stepsObservationDto.setPatientID(ref);
        stepsObservationDto.setFhirServer(fhirServer);

        try {
            String dateStr = observation.getCode().getCoding().get(0).getDisplay().replace("Number of Steps in 24 Hours, Measured at ", "");
            System.out.println("Date String: " + dateStr);
            LocalDate date = LocalDate.parse(dateStr);
            stepsObservationDto.setDate(date);
        } catch (DateTimeParseException exception) {
            System.out.println("Could not parse date time");
            stepsObservationDto.setDate(null);
        } catch (NullPointerException nullPointerException) {
            System.out.println("Could not parse date time (null pointer)");
            stepsObservationDto.setDate(null);
        }

        String deviceRef = observation.getDevice().getReference();
        if (deviceRef.contains("/")) {
            var refParts = deviceRef.split("/");
            deviceRef = refParts[refParts.length - 1];
        }
        stepsObservationDto.setDeviceID(deviceRef);

        // Setting value
        Quantity value = (Quantity) observation.getValue();
        int steps = value.getValue().intValue();
        stepsObservationDto.setSteps(steps);
        return stepsObservationDto;
    }

    public WeightObservationDTO getWeightObservationById(String id) {
        String fhirServer = settingsController.getFhirServerUrl();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Bundle bundle = searchObservationById(id, fhirServer, client);
        if (bundle.getEntryFirstRep() == null) {
            return null;
        }

        Observation observation = (Observation) bundle.getEntryFirstRep().getResource();

        Patient patient = (Patient) patientController.searchPatientById(observation.getSubject().getReference(), fhirServer, client).getEntryFirstRep().getResource();
        WeightObservationDTO weightObservationDTO = convertObservationToWeightObservation(fhirServer, patient, observation);

        return weightObservationDTO;
    }

    public Bundle searchObservationById(String id, String fhirServer, IGenericClient client) {
        String searchUrl = fhirServer + "/Observation?_id=" + id;
        Bundle response = client.search()
            .byUrl(searchUrl)
            .returnBundle(Bundle.class)
            .execute();
        if (response.getTotal() <= 0) {
            return null;
        }
        return response;
    }

    public StepsObservationDTO getStepsObservationById(String id) {
        String fhirServer = settingsController.getFhirServerUrl();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Bundle bundle = searchObservationById(id, fhirServer, client);
        if (bundle.getEntryFirstRep() == null) {
            return null;
        }

        Observation observation = (Observation) bundle.getEntryFirstRep().getResource();

        StepsObservationDTO stepsObservationDTO = convertObservationToStepsDto(fhirServer, null, observation);

        return stepsObservationDTO;
    }

    public List<WeightObservationDTO> getWeightsByDate(DateDTO dateDTO) {
        String fhirServer = settingsController.getFhirServerUrl();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        String id = dateDTO.getPatientId();
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

            // Skipping wrong dates
            if (!observation.getCode().getCoding().get(0).getDisplay().contains(dateDTO.getDate().toString())) {
                continue;
            }
            WeightObservationDTO weightObservationDTO = convertObservationToWeightObservation(fhirServer, patient, observation);

            retObervations.add(weightObservationDTO);
        }

        return retObervations;
    }

    public List<StepsObservationDTO> getStepsByDate(DateDTO dateDTO) {
        String fhirServer = settingsController.getFhirServerUrl();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        String id = dateDTO.getPatientId();
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

            // Skipping step observations
            if (!observation.getCode().getCoding().get(0).getDisplay().contains("Number of Steps in 24 Hours, Measured at ")) {
                continue;
            }

            // Skipping wrong dates
            if (!observation.getCode().getCoding().get(0).getDisplay().contains(dateDTO.getDate().toString())) {
                continue;
            }
            StepsObservationDTO weightObservationDTO = convertObservationToStepsDto(fhirServer, patient, observation);

            retObervations.add(weightObservationDTO);
        }

        return retObervations;
    }
}
