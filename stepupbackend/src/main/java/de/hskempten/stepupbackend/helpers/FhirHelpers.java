package de.hskempten.stepupbackend.helpers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.Resource;

public class FhirHelpers {

    public static void PrettyPrint(Resource resource, FhirContext context) {
        IParser parser = (IParser) context.newJsonParser();
        parser.setPrettyPrint(true);
        System.out.println(parser.encodeResourceToString(resource));
    }
}
