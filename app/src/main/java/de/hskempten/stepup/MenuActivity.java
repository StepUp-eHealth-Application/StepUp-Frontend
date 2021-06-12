package de.hskempten.stepup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import de.hskempten.stepup.helpers.APIEndpoints;
import de.hskempten.stepup.preferences.Preferences;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivity";

    TextView txtTitle;
    LinearLayout viewSettings;
    ImageButton btnSettings;
    String patientId;

    RequestQueue requestQueue;

    int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // getting patient
        patientId = Preferences.loadSelectedPatientID(getApplicationContext());
        if (patientId == null || patientId == "") {
            patientId = Preferences.loadActualPatientID(getApplicationContext());
        }
        txtTitle = findViewById(R.id.txtTitle);
        String backendUrlName = getBackendUrlName();
        setupQueue();
        getPatientNameFromBackend(backendUrlName);

        // setting variables
        btnSettings = findViewById(R.id.btnSettings);
        viewSettings = findViewById(R.id.viewSettings);

        // hiding settings
        viewSettings.setVisibility(View.GONE);

        // setting the OnClick Listener
        btnSettings.setOnClickListener(v -> toggleVisibility());

        // Opening Summary Activity
        Button btnSummary = findViewById(R.id.btnSummary);
        btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SummaryActivity.class);
                MenuActivity.this.startActivity(intent);
            }
        });

        // Opening Patient Data Activity
        Button btnPatientData = findViewById(R.id.btnPatientData);
        btnPatientData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, PatientDataActivity.class);
                startActivity(intent);
            }
        });

        // Opening FHIR Settings Activity when "Servereinstellungen" Button clicked
        Button btnFHIRServerSettings = findViewById(R.id.btnFHIRServerSettings);
        btnFHIRServerSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(MenuActivity.this, SettingsActivity.class);
                MenuActivity.this.startActivity(settingsIntent);
            }
        });
    }

    protected void toggleVisibility() {
        if (viewSettings.getVisibility() == View.GONE) viewSettings.setVisibility(View.VISIBLE);
        else viewSettings.setVisibility(View.GONE);
    }

    private void setupQueue() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    private String getBackendUrlName() {
        String backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.PATIENT + patientId;
        Log.d(TAG, "Backend URL: " + backendUrl);
        return backendUrl;
    }

    private void getPatientNameFromBackend(String backendUrl) {
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.GET, backendUrl, new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String firstName = response.getString("firstName");
                            String lastName = response.getString("lastName");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onResponse: success");
                        changeName();
                        Toast.makeText(getApplicationContext(), "Patient erfolgreich geladen!", duration).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onResponse: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Patient konnte nicht geladen werden!", duration).show();
                    }
                });
        requestQueue.add(jsonobj);
    }

    private void changeName() {
        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText(patientId);
    }
}
