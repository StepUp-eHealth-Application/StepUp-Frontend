package de.hskempten.stepupbackend.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepupbackend.dto.StepsGoalDTO;
import de.hskempten.stepupbackend.dto.WeightGoalDTO;
import de.hskempten.stepupbackend.helpers.FhirHelpers;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
public class GoalController {

    @Autowired
    private PatientController patientController;

    @Autowired
    private SettingsController settingsController;

    public List<WeightGoalDTO> getAllWeightGoalsByPatientId(String patientId) {
        String fhirServer = settingsController.getFhirServerUrl();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Patient patient =
            (Patient) patientController.searchPatientById(patientId, fhirServer, client)
                .getEntryFirstRep().getResource();

        Bundle goalsBundle = client.search()
            .forResource(Goal.class)
            .where(Observation.SUBJECT.hasId(patient.getIdElement().getValue()))
            .returnBundle(Bundle.class)
            .execute();

        List<WeightGoalDTO> retGoals = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : goalsBundle.getEntry()) {
            Goal goal = (Goal) entry.getResource();

            if (goal.getDescription().getCoding().get(0).getDisplay().contains("Gesundheitsziel Schritte")) {
                continue;
            }

            WeightGoalDTO weightGoalDTO = convertGoalToWeightGoalDto(goal, patientId);
            retGoals.add(weightGoalDTO);
        }

        return retGoals;
    }

    private WeightGoalDTO convertGoalToWeightGoalDto(Goal goal, String patientId) {
        WeightGoalDTO weightGoalDTO = new WeightGoalDTO();

        weightGoalDTO.setId(goal.getIdElement().getIdPart());
        weightGoalDTO.setDescription(goal.getDescription().getCodingFirstRep().getDisplay());
        weightGoalDTO.setPatientId(patientId);

        // Setting due date
        var dueDate = goal.getTarget().get(0).getDue().dateTimeValue();
        Date date = new Date();
        date.setDate(dueDate.getDay());
        date.setMonth(dueDate.getMonth());
        date.setYear(dueDate.getValue().getYear());
        weightGoalDTO.setDueDate(date);

        weightGoalDTO.setWeightGoal(
            goal.getTargetFirstRep().getDetailQuantity().getValue().floatValue()
        );

        return weightGoalDTO;
    }

    public WeightGoalDTO addWeightGoal(WeightGoalDTO weightGoalDTO) {
        String fhirServer = settingsController.getFhirServerUrl();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Patient patient = (Patient) patientController.searchPatientById(weightGoalDTO.getPatientId(), fhirServer, client).getEntryFirstRep().getResource();
        Goal goal = new Goal();

        CodeableConcept concept = new CodeableConcept();
        concept.addCoding().setDisplay("Gesundheitsziel Gewicht");
        goal.setDescription(concept);

        Reference patientRef = new Reference(patient.getIdElement().getValue());
        patientRef.setType("Patient");
        goal.setSubject(patientRef);

        goal.addTarget()
                .setDetail(
                    new Quantity()
                        .setValue(weightGoalDTO.getWeightGoal())
                        .setUnit("kg")
                        .setSystem("http://unitsofmeasure.org")
                )
                .setDue(
                    new DateType().setValue(weightGoalDTO.getDueDate())
                );

        goal.setLifecycleStatus(Goal.GoalLifecycleStatus.ACTIVE);
        goal.addCategory().addCoding()
            .setSystem("http://terminology.hl7.org/CodeSystem/goal-category")
            .setCode("behavioral")
            .setDisplay("Goals related to the manner in which the subject acts.");

        Goal createGoal = (Goal) client.create().resource(goal).execute().getResource();
        FhirHelpers.PrettyPrint(createGoal, ctx);

        weightGoalDTO.setId(createGoal.getIdElement().getIdPart());

        return weightGoalDTO;
    }

    public WeightGoalDTO updateWeightGoal(String id, WeightGoalDTO weightGoalDTO) {
        weightGoalDTO.setId(id);
        String fhirServer = settingsController.getFhirServerUrl();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Patient patient = (Patient) patientController.searchPatientById(weightGoalDTO.getPatientId(), fhirServer, client).getEntryFirstRep().getResource();

        Goal goal = (Goal) searchGoalById(id, fhirServer, client).getEntryFirstRep().getResource();

        Reference patientRef = new Reference(patient.getIdElement().getValue());
        patientRef.setType("Patient");
        goal.setSubject(patientRef);

        goal.getTarget().get(0)
            .setDetail(
                new Quantity()
                    .setValue(weightGoalDTO.getWeightGoal())
                    .setUnit("kg")
                    .setSystem("http://unitsofmeasure.org")
            )
            .setDue(
                new DateType().setValue(weightGoalDTO.getDueDate())
            );

        goal.setLifecycleStatus(Goal.GoalLifecycleStatus.ACTIVE);  // TODO: change

        Goal createGoal = (Goal) client.update().resource(goal).execute().getResource();
        FhirHelpers.PrettyPrint(createGoal, ctx);

        return weightGoalDTO;
    }

    public Bundle searchGoalById(String id, String fhirServer, IGenericClient client) {
        String searchUrl = fhirServer + "/Goal?_id=" + id;
        Bundle response = client.search()
            .byUrl(searchUrl)
            .returnBundle(Bundle.class)
            .execute();
        if (response.getTotal() <= 0) {
            return null;
        }
        return response;
    }

    public List<StepsGoalDTO> getAllStepsGoalsByPatientId(String patientId) {
        String fhirServer = settingsController.getFhirServerUrl();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Patient patient =
            (Patient) patientController.searchPatientById(patientId, fhirServer, client)
                .getEntryFirstRep().getResource();

        Bundle goalsBundle = client.search()
            .forResource(Goal.class)
            .where(Observation.SUBJECT.hasId(patient.getIdElement().getValue()))
            .returnBundle(Bundle.class)
            .execute();

        List<StepsGoalDTO> retGoals = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : goalsBundle.getEntry()) {
            Goal goal = (Goal) entry.getResource();

            if (!goal.getDescription().getCoding().get(0).getDisplay().contains("Gesundheitsziel Schritte")) {
                continue;
            }

            StepsGoalDTO stepsGoalDTO = convertGoalToStepsDTO(goal, patientId);
            retGoals.add(stepsGoalDTO);
        }

        return retGoals;
    }

    private StepsGoalDTO convertGoalToStepsDTO(Goal goal, String patientId) {
        StepsGoalDTO stepsGoalDTO = new StepsGoalDTO();

        stepsGoalDTO.setId(goal.getIdElement().getIdPart());
        stepsGoalDTO.setDescription(
            goal.getDescription().getCodingFirstRep().getDisplay()
        );
        stepsGoalDTO.setPatientId(patientId);
        stepsGoalDTO.setDueDate(goal.getTarget().get(0).getDue().dateTimeValue().getValue());
        stepsGoalDTO.setStepsGoal(
            goal.getTargetFirstRep().getDetailQuantity().getValue().intValue()
        );

        return stepsGoalDTO;
    }

    public StepsGoalDTO addStepsGoal(StepsGoalDTO stepsGoalDTO) {
        String fhirServer = settingsController.getFhirServerUrl();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Patient patient = (Patient) patientController.searchPatientById(stepsGoalDTO.getPatientId(), fhirServer, client).getEntryFirstRep().getResource();
        Goal goal = new Goal();

        CodeableConcept concept = new CodeableConcept();
        concept.addCoding().setDisplay("Gesundheitsziel Schritte");
        goal.setDescription(concept);

        Reference patientRef = new Reference(patient.getIdElement().getValue());
        patientRef.setType("Patient");
        goal.setSubject(patientRef);

        goal.addTarget()
            .setDetail(
                new Quantity()
                    .setValue(stepsGoalDTO.getStepsGoal())
                    .setUnit("Number of steps in 24 hour Measured")
                    .setSystem("https://loinc.org/41950-7/")
                    .setCode("41950-7")
            )
            .setDue(
                new DateType().setValue(stepsGoalDTO.getDueDate())
            );

        goal.setLifecycleStatus(Goal.GoalLifecycleStatus.ACTIVE);
        goal.addCategory().addCoding()
            .setSystem("http://terminology.hl7.org/CodeSystem/goal-category")
            .setCode("behavioral")
            .setDisplay("Goals related to the manner in which the subject acts.");

        Goal createGoal = (Goal) client.create().resource(goal).execute().getResource();
        FhirHelpers.PrettyPrint(createGoal, ctx);

        stepsGoalDTO.setId(createGoal.getIdElement().getIdPart());

        return stepsGoalDTO;
    }

    public StepsGoalDTO updateStepsGoal(String id, StepsGoalDTO stepsGoalDTO) {
        String fhirServer = settingsController.getFhirServerUrl();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Patient patient = (Patient) patientController.searchPatientById(stepsGoalDTO.getPatientId(), fhirServer, client).getEntryFirstRep().getResource();
        Goal goal = (Goal) searchGoalById(id, fhirServer, client).getEntryFirstRep().getResource();

        Reference patientRef = new Reference(patient.getIdElement().getValue());
        patientRef.setType("Patient");
        goal.setSubject(patientRef);

        goal.getTarget().get(0)
            .setDetail(
                new Quantity()
                    .setValue(stepsGoalDTO.getStepsGoal())
                    .setUnit("Number of steps in 24 hour Measured")
                    .setSystem("https://loinc.org/41950-7/")
                    .setCode("41950-7")
            )
            .setDue(
                new DateType().setValue(stepsGoalDTO.getDueDate())
            );

        goal.setLifecycleStatus(Goal.GoalLifecycleStatus.ACTIVE); // TODO: change

        Goal updateGoal = (Goal) client.update().resource(goal).execute().getResource();
        FhirHelpers.PrettyPrint(updateGoal, ctx);

        stepsGoalDTO.setId(id);

        return stepsGoalDTO;
    }

    public WeightGoalDTO getWeightGoalById(String id) {
        String fhirServer = settingsController.getFhirServerUrl();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Bundle goal = searchGoalById(id, fhirServer, client);
        if (goal.getEntryFirstRep() == null) {
            return null;
        }

        Goal g = (Goal) goal.getEntryFirstRep().getResource();
        String patientId = g.getSubject().getReference().split("/")[1];
        WeightGoalDTO weightGoalDTO = convertGoalToWeightGoalDto((Goal) goal.getEntryFirstRep().getResource(), patientId);
        return weightGoalDTO;
    }

    public StepsGoalDTO getStepsGoalById(String id) {
        String fhirServer = settingsController.getFhirServerUrl();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Bundle goal = searchGoalById(id, fhirServer, client);
        if (goal.getEntryFirstRep() == null) {
            return null;
        }

        Goal g = (Goal) goal.getEntryFirstRep().getResource();
        String patientId = g.getSubject().getReference().split("/")[1];
        StepsGoalDTO stepsGoalDto = convertGoalToStepsDTO(g, patientId);
        return stepsGoalDto;
    }
}
