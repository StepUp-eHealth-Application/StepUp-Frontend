package de.hskempten.stepupbackend.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepupbackend.dto.CompositionDTO;
import de.hskempten.stepupbackend.fhir.FhirClient;
import de.hskempten.stepupbackend.helpers.FhirHelpers;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        composition.setStatus(Composition.CompositionStatus.FINAL);

        CodeableConcept typeCC = new CodeableConcept();
        typeCC.addCoding()
            .setCode("11506-3")
            .setSystem("http://loinc.org")
            .setDisplay("Progress note");
        composition.setType(typeCC);

        CodeableConcept categoryCC = new CodeableConcept();
        categoryCC.addCoding()
            .setCode("53576-5")
            .setSystem("http://loinc.org")
            .setDisplay("Personal health monitoring report Document");

        composition.setDate(compositionDTO.getDate());

        Reference patientReference = new Reference(patient);
        patientReference.setType("Patient");
        composition.setSubject(patientReference);

        List<Reference> authors = new ArrayList<>();
        authors.add(patientReference);
        composition.setAuthor(authors);

        composition.setTitle(
            "Zusammenfassung für " +
                patient.getNameFirstRep().getGivenAsSingleString() + " " +
                patient.getNameFirstRep().getFamily()
        );

        composition.setConfidentiality(Composition.DocumentConfidentiality.N);

        // Adding goals section
        Bundle goalsBundle = client.search()
            .forResource(Goal.class)
            .where(Observation.SUBJECT.hasId(patient.getIdElement().getValue()))
            .returnBundle(Bundle.class)
            .execute();

        List<Reference> goalReferences = new ArrayList<>();
        for (var entry : goalsBundle.getEntry()) {
            Goal goal = (Goal) entry.getResource();

            Reference reference = new Reference(goal);
            reference.setType("Goal");

            goalReferences.add(reference);
        }

        CodeableConcept codeCC = new CodeableConcept();
        codeCC.addCoding()
            .setDisplay("Planned procedure Narrative")
            .setCode("59772-4")
            .setSystem("http://loinc.org");

        composition.addSection()
            .setTitle("Gesundheitsziele des Patienten")
            .setCode(codeCC)
            .setAuthor(authors)
            .setEntry(goalReferences);

        // Adding observation section
        Bundle observationBundle = client.search()
            .forResource(Observation.class)
            .where(Observation.SUBJECT.hasId(patient.getIdElement().getValue()))
            .returnBundle(Bundle.class)
            .execute();

        List<Reference> observationReference = new ArrayList<>();
        for (var entry : observationBundle.getEntry()) {
            Observation observation = (Observation) entry.getResource();

            Reference reference = new Reference(observation);
            reference.setType("Observation");

            observationReference.add(reference);
        }

        CodeableConcept codeObservationCC = new CodeableConcept();
        codeObservationCC.addCoding()
            .setDisplay("Objective Narrative")
            .setCode("61149-1")
            .setSystem("http://loinc.org");

        composition.addSection()
            .setTitle("Beobachtungen des Patienten")
            .setCode(codeCC)
            .setAuthor(authors)
            .setEntry(observationReference);

        Composition createdComposition = (Composition) client.create().resource(composition).execute().getResource();
        FhirHelpers.PrettyPrint(createdComposition, ctx);

        compositionDTO.setId(createdComposition.getIdElement().getIdPart());
        return compositionDTO;
    }
}
