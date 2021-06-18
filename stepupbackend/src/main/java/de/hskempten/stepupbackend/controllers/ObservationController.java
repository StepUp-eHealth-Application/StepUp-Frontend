package de.hskempten.stepupbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepupbackend.dto.DateDTO;
import de.hskempten.stepupbackend.dto.StepsObservationDTO;
import de.hskempten.stepupbackend.dto.WeightObservationDTO;
import de.hskempten.stepupbackend.helpers.FhirHelpers;

@Controller
public class ObservationController {

    @Autowired
    private PatientController patientController;

    @Autowired
    private SettingsController settingsController;

    @Autowired
    private DeviceController deviceController;

    @Autowired
    private PractitionerController practitionerController;

    @Autowired
    private GoalController goalController;

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

        // Setting device reference
        String deviceId = weightObservationDTO.getDeviceID();
        if (deviceId != null && !deviceId.isEmpty()) {
            var deviceEntry = deviceController.searchDeviceById(deviceId, weightObservationDTO.getFhirServer(), client).getEntryFirstRep();
            if (deviceEntry != null) {
                Device device = (Device) deviceEntry.getResource();

                Reference deviceRef = new Reference(device);
                deviceRef.setType("Device");
                observation.setDevice(deviceRef);
            }
        }

        // Setting concept
        observation
            .getCode()
                .addCoding()
                    .setSystem("http://loinc.org")
                    .setCode("29463-7")
                    .setDisplay("Body weight measured at " + weightObservationDTO.getDate().toString() + ", for Goal with ID: " + weightObservationDTO.getGoalID());

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
        weightObservationDTO.setGoalID("-1");
        
        String patientId = observation.getSubject().getReference();
        if (patientId != null && !patientId.isEmpty() && patientId.contains("/")) {
            var patientParts = patientId.split("/");
            patientId = patientParts[patientParts.length - 1];
        }
        weightObservationDTO.setPatientID(patientId);
        weightObservationDTO.setFhirServer(fhirServer);

        try {
            String dateStr = observation.getCode().getCoding().get(0).getDisplay().replace("Body weight measured at ", "");

            if (dateStr.contains(", for Goal with ID: ")) {
                var splits = dateStr.split(", for Goal with ID: ");
                dateStr = splits[0];
                String goalId = splits[1];
                weightObservationDTO.setGoalID(goalId);
            }

            System.out.println("Date String: " + dateStr);
            LocalDate date = LocalDate.parse(dateStr);
            weightObservationDTO.setDate(date);
        } catch (DateTimeParseException exception) {
            System.out.println("Could not parse date time");
            weightObservationDTO.setDate(null);
        }

        String deviceId = observation.getDevice().getReference();
        if (deviceId != null && !deviceId.isEmpty() && deviceId.contains("/")) {
            var deviceParts = deviceId.split("/");
            deviceId = deviceParts[deviceParts.length - 1];
        }
        weightObservationDTO.setDeviceID(deviceId);

        // Setting value
        Quantity value = (Quantity) observation.getValue();
        float steps = value.getValue().floatValue();
        weightObservationDTO.setWeight(steps);
        return weightObservationDTO;
    }

    public StepsObservationDTO addStepsObservation(StepsObservationDTO stepsObservationDTO) {
        FhirContext ctx = FhirContext.forR4();
        String serverBase = settingsController.getFhirServerUrl();

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

        String deviceId = stepsObservationDTO.getDeviceID();
        if (deviceId != null && !deviceId.isEmpty()) {
            // Setting device reference
            Device device = (Device) deviceController
                .searchDeviceById(stepsObservationDTO.getDeviceID(), stepsObservationDTO.getFhirServer(), client)
                .getEntryFirstRep()
                .getResource();

            Reference deviceRef = new Reference(device);
            deviceRef.setType("Device");
            observation.setDevice(deviceRef);
        }

        // Setting concept
        observation
            .getCode()
            .addCoding()
            .setSystem("http://loinc.org")
            .setCode("41950-7")
            .setDisplay("Number of Steps in 24 Hours, Measured at " + stepsObservationDTO.getDate().toString() + ", for Goal with ID: " + stepsObservationDTO.getGoalID());

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
            String dateStr = observation.getCode().getCoding().get(0).getDisplay();
            dateStr = dateStr.replace("Number of Steps in 24 Hours, Measured at ", "");

            if (dateStr.contains(", for Goal with ID: ")) {
                var parts = dateStr.split(", for Goal with ID: ");
                dateStr = parts[0];
                String goalId = parts[1];
                stepsObservationDto.setGoalID(goalId);
            }

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

        var dRef = observation.getDevice();
        if (dRef != null) {
            String deviceRef = observation.getDevice().getReference();
            if (deviceRef != null) {
                if (deviceRef.contains("/")) {
                    var refParts = deviceRef.split("/");
                    deviceRef = refParts[refParts.length - 1];
                }
                stepsObservationDto.setDeviceID(deviceRef);
            }
        }

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

        WeightObservationDTO weightObservationDTO = convertObservationToWeightObservation(fhirServer, null, observation);

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

    public List<StepsObservationDTO> getStepsByGoalId(String goalId, String patientId) {
        String fhirServer = settingsController.getFhirServerUrl();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Bundle observations = client.search().forResource(Observation.class)
            .where(Observation.SUBJECT.hasId(patientId))
            .returnBundle(Bundle.class).execute();

        List<StepsObservationDTO> retObervations = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : observations.getEntry()) {
            Observation observation = (Observation) entry.getResource();

            // Skipping weight observations
            if (observation.getCode() == null
                || observation.getCode().getCoding() == null
                || observation.getCode().getCoding().get(0) == null
                || observation.getCode().getCoding().get(0).getDisplay() == null) {
                continue;
            }
            if (!observation.getCode().getCoding().get(0).getDisplay().contains("Number of Steps in 24 Hours, Measured at ")) {
                continue;
            }

            StepsObservationDTO stepsObservationDto = convertObservationToStepsDto(fhirServer, null, observation);

            if (stepsObservationDto.getGoalID().equals(goalId)) {
                retObervations.add(stepsObservationDto);
            }
        }

        return retObervations;
    }

    public List<WeightObservationDTO> getWeightsByGoalId(String goalId, String patientId) {
        String fhirServer = settingsController.getFhirServerUrl();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Bundle observations = client.search().forResource(Observation.class)
            .where(Observation.SUBJECT.hasId(patientId))
            .returnBundle(Bundle.class).execute();

        List<WeightObservationDTO> retObervations = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : observations.getEntry()) {
            Observation observation = (Observation) entry.getResource();

            // Skipping weight observations
            if (observation.getCode() == null
                || observation.getCode().getCoding() == null
                || observation.getCode().getCoding().get(0) == null
                || observation.getCode().getCoding().get(0).getDisplay() == null) {
                continue;
            }
            if (observation.getCode().getCoding().get(0).getDisplay().contains("Number of Steps in 24 Hours, Measured at ")) {
                continue;
            }

            WeightObservationDTO stepsObservationDto = convertObservationToWeightObservation(fhirServer, null, observation);

            if (stepsObservationDto.getGoalID().equals(goalId)) {
                retObervations.add(stepsObservationDto);
            }
        }

        return retObervations;
    }
}
