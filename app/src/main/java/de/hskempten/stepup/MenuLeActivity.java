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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import de.hskempten.stepup.helpers.APIEndpoints;
import de.hskempten.stepup.preferences.Preferences;

public class MenuLeActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MenuLeActivity";

    TextView txtTitle;
    LinearLayout viewSettings;
    ImageButton btnSettings;
    String patientId;
    String patientName;

    FloatingActionButton btnNewObservation;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_le);

        // getting patient
        patientId = Preferences.loadSelectedPatientID(getApplicationContext());
        txtTitle = findViewById(R.id.txtTitle);
        String backendServer = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.PATIENT + patientId;
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.GET, backendServer, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String id = "";
                        patientName = "";
                        try {
                            id = response.getString("id");
                            patientName = response.getString("firstName") + " " + response.getString("lastName");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        txtTitle.setText("Gesundheitsziele von\n" + patientName);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG, "onResponse: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Fehler beim Laden des Patientennamens", Toast.LENGTH_LONG).show();
                        txtTitle.setText("Kein Patient ausgewählt");
                    }
                }
        ){

        };
        requestQueue.add(jsonobj);

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
                Intent intent = new Intent(MenuLeActivity.this, SummaryActivity.class);
                startActivity(intent);
            }
        });

        // Opening Patient Data Activity
        Button btnPatientData = findViewById(R.id.btnPatientData);
        btnPatientData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuLeActivity.this, DisplayPatientDataActivity.class);
                startActivity(intent);
            }
        });

        btnNewObservation = findViewById(R.id.btnNewObservation2);
        Log.d("foo", "onCreate: " + btnNewObservation);
        btnNewObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getApplicationContext(), ChooseObservationType.class);
                MenuLeActivity.this.startActivity(settingsIntent);
            }
        });
    }

    protected void toggleVisibility() {
        if (viewSettings.getVisibility() == View.GONE) viewSettings.setVisibility(View.VISIBLE);
        else viewSettings.setVisibility(View.GONE);
    }
}
