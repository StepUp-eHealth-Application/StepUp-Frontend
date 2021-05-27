package de.hskempten.stepup.fhir;

import android.app.Activity;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepup.preferences.Preferences;

public class FhirClient {

    private static FhirClient instance;

    private FhirContext ctx;
    private IGenericClient client;
    private String url;

    public static FhirClient getInstance(Activity activity) {
        if (instance == null) {
            instance = new FhirClient(activity);
        }

        return instance;
    }

    public IGenericClient getClient() {
        return client;
    }

    private FhirClient(Activity activity) {
        ctx = FhirContext.forR4();
        url = Preferences.loadFhirServerUrl(activity);
        client = ctx.newRestfulGenericClient(url);
    }
}
