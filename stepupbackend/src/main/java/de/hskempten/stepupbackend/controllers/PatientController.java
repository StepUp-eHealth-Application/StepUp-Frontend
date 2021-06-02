package de.hskempten.stepupbackend.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepupbackend.dto.PatientDTO;
import de.hskempten.stepupbackend.helpers.FhirHelpers;
import org.hl7.fhir.r4.model.Bundle;
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
        FhirHelpers.PrettyPrint(createPatient, ctx);

        patientDTO.setId(createPatient.getIdElement().getIdPart());

        return patientDTO;
    }

    public PatientDTO getPatientById(String id) {
        String fhirServer = "http://hapi.fhir.org/baseR4/"; // TODO: get fhir server from database


        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Bundle response = searchPatientById(id, fhirServer, client);
        if (response == null) return null;

        Patient retPatient = (Patient) response.getEntryFirstRep().getResource();
        FhirHelpers.PrettyPrint(retPatient, ctx);

        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(retPatient.getIdElement().getIdPart());

        patientDTO.setFirstName(retPatient.getNameFirstRep().getGivenAsSingleString());
        patientDTO.setLastName(retPatient.getNameFirstRep().getFamily());

        HashMap<Enumerations.AdministrativeGender, String> genders = new HashMap<>();
        genders.put(Enumerations.AdministrativeGender.MALE, "Männlich");
        genders.put(Enumerations.AdministrativeGender.FEMALE, "Weiblich");
        genders.put(Enumerations.AdministrativeGender.OTHER, "Divers");
        genders.put(Enumerations.AdministrativeGender.UNKNOWN, "Unbekannt");
        patientDTO.setGender(genders.get(retPatient.getGender()));

        patientDTO.setCity(retPatient.getAddressFirstRep().getCity());
        patientDTO.setCountry(retPatient.getAddressFirstRep().getCountry());
        patientDTO.setPostalCode(retPatient.getAddressFirstRep().getPostalCode());

        if (retPatient.getAddressFirstRep() != null && retPatient.getAddressFirstRep().getText() != null) {
            String[] addr = retPatient.getAddressFirstRep().getText().split(",");
            if (addr != null && addr.length > 0) {
                patientDTO.setStreet(addr[0]);
            }
        }

        patientDTO.setFhirServer(fhirServer);

        return patientDTO;
    }

    public Bundle searchPatientById(String id, String fhirServer, IGenericClient client) {
        String searchUrl = fhirServer + "/Patient?_id=" + id;
        Bundle response = client.search()
            .byUrl(searchUrl)
            .returnBundle(Bundle.class)
            .execute();
        if (response.getTotal() <= 0) {
            return null;
        }
        return response;
    }
}
