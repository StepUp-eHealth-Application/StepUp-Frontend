package de.hskempten.stepup.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepup.preferences.Preferences;

public class FhirClient {

    private static FhirClient instance;

    private FhirContext ctx;
    private IGenericClient client;

    public static FhirClient getInstance() {
        if (instance == null) {
            instance = new FhirClient();
        }

        return instance;
    }

    public IGenericClient getClient() {
        return client;
    }

    private FhirClient() {
        ctx = FhirContext.forR4();
        client = ctx.newRestfulGenericClient(Preferences.loadFhirServerUrl());
    }
}
