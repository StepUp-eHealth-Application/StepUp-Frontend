package de.hskempten.stepup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ca.uhn.fhir.util.UrlUtil;
import de.hskempten.stepup.helpers.APIEndpoints;
import de.hskempten.stepup.preferences.Preferences;

public class SettingsActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Settings";

    EditText txtFhirServerURL;
    EditText txtBackendUrl;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        txtFhirServerURL = findViewById(R.id.etxtFhirServer);
        txtBackendUrl = findViewById(R.id.eTxtBackendServer);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // Displaying FHIR Server URL if already provided
        String fhirServerURL = Preferences.loadFhirServerUrl(SettingsActivity.this);
        if (fhirServerURL != null) {
                txtFhirServerURL.setText(fhirServerURL);
        } else {
            txtFhirServerURL.setError("FHIR Server URL muss mit einem \"/\" enden");
        }

        // Displaying Backend URL if already provided
        String backendUrl = Preferences.loadBackendUrl(SettingsActivity.this);
        if (backendUrl != null) {
            txtBackendUrl.setText(backendUrl);
        }

        // Saving FHIR Server URL into preference
        Button btnSave = findViewById(R.id.btnSaveSettings);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence text;
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                String url = txtFhirServerURL.getText().toString();
                String backendUrl = txtBackendUrl.getText().toString();

                boolean checked = true;
                if (!UrlUtil.isValid(url)) {
                    txtFhirServerURL.setError("URL ist nicht valide");
                    checked = false;
                }

                if (!(fhirServerURL.endsWith("/"))) {
                    txtFhirServerURL.setError("URL muss mit einem \"/\" enden");
                    checked = false;
                }

                if (!UrlUtil.isValid(backendUrl)) {
                    txtBackendUrl.setError("URL ist nicht valide");
                    checked = false;
                }

                if (!(backendUrl.endsWith("/"))) {
                    txtFhirServerURL.setError("URL muss mit einem \"/\" enden");
                    checked = false;
                }

                // Checking if given URL is valid
                if (checked) {
                    Preferences.saveFhirServerUrl(url, SettingsActivity.this);
                    Preferences.saveBackendUrl(backendUrl, SettingsActivity.this);

                    String backendServer = backendUrl + APIEndpoints.SETTINGS;

                    HashMap<String, String> data = new HashMap<>();
                    data.put("fhirUrl", url);
                    // Sending data to backend
                    JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.GET, backendServer, new JSONObject(data),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i(LOG_TAG, "onResponse: success");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i(LOG_TAG, "onResponse: " + error.getMessage());
                                }
                            }
                    ){

                    };
                    requestQueue.add(jsonobj);

                    Toast.makeText(context, "URLs wurden gespeichert", duration).show();
                }


            }
        });
    }
}