package de.hskempten.stepupbackend.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepupbackend.dto.PatientDTO;
import de.hskempten.stepupbackend.dto.PractitionerDTO;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PractitionerController {

    @Autowired
    private SettingsController settingsController;

    public List<PractitionerDTO> getAllPractitioners() {
        String fhirServer = settingsController.getFhirServerUrl();

        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        Bundle bundle = client
            .search()
            .forResource(Practitioner.class)
            .returnBundle(Bundle.class)
            .execute();

        List<PractitionerDTO> retPractitioners = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            Practitioner retPractitioner = (Practitioner) entry.getResource();

            PractitionerDTO patientDTO = convertToPractitionerDTO(retPractitioner);
            retPractitioners.add(patientDTO);
        }

        return retPractitioners;
    }

    private PractitionerDTO convertToPractitionerDTO(Practitioner retPractitioner) {
        PractitionerDTO practitionerDTO = new PractitionerDTO();

        String id = retPractitioner.getIdElement().getIdPart();
        if (id.contains("/")) {
            var idParts = id.split("/");
            id = idParts[idParts.length - 1];
        }
        practitionerDTO.setId(id);

        String firstName = retPractitioner.getNameFirstRep().getGivenAsSingleString();
        String lastName = retPractitioner.getNameFirstRep().getFamily();
        practitionerDTO.setFirstName(firstName);
        practitionerDTO.setLastName(lastName);

        return practitionerDTO;
    }

    public Bundle searchPractitionerById(String id, String fhirServer, IGenericClient client) {
        String searchUrl = fhirServer + "/Practitioner?_id=" + id;
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
