package de.hskempten.stepupbackend.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepupbackend.dto.PatientDTO;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.HashMap;


@Controller
public class PatientController {

    public PatientDTO createPatient(PatientDTO patientDTO) {
        FhirContext ctx = FhirContext.forR4();
        String serverBase = patientDTO.getFhirServer();

        IGenericClient client = ctx.newRestfulGenericClient(serverBase);

        Patient patient = new Patient();
        patient.addName()
            .addGiven(patientDTO.getFirstName())
            .setFamily(patientDTO.getLastName())
            .setUse(HumanName.NameUse.OFFICIAL);

        patient.addAddress()
            .setCity(patientDTO.getCity())
            .setCountry(patientDTO.getCountry())
            .setText(patientDTO.getDisplayAddress())
            .setPostalCode(patientDTO.getPostalCode());

        HashMap<String, Enumerations.AdministrativeGender> genders = new HashMap<>();
        genders.put("Männlich", Enumerations.AdministrativeGender.MALE);
        genders.put("Weiblich", Enumerations.AdministrativeGender.FEMALE);
        genders.put("Divers", Enumerations.AdministrativeGender.OTHER);
        genders.put("Unbekannt", Enumerations.AdministrativeGender.UNKNOWN);

        patient.setGender(genders.get(patientDTO.getGender()));

        Patient createPatient = (Patient) client.create().resource(patient).execute().getResource();

        patientDTO.setId(createPatient.getIdElement().getIdPart());

        return patientDTO;
    }
}
