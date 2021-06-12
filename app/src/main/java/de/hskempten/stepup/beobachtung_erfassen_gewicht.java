package de.hskempten.stepup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import de.hskempten.stepup.helpers.APIEndpoints;
import de.hskempten.stepup.preferences.Preferences;

public class beobachtung_erfassen_gewicht extends AppCompatActivity {

    private final static String TAG = "b_erfassen_gewicht";
    private final static int duration = Toast.LENGTH_SHORT;

    EditText eTxtNumber;
    DatePicker dpObservationWeight;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beobachtung_erfassen_gewicht);

        findViews();
        initSaveButton();
    }

    private void findViews() {
        eTxtNumber = findViewById(R.id.eTxtWeightObservation);
        dpObservationWeight = findViewById(R.id.dpWeightObservationDate);
        save = findViewById(R.id.btnSaveWeightObservation);
    }

    private void initSaveButton() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Button click");

                if (eTxtNumber.getText() == null) {
                    eTxtNumber.setError("Geben Sie bitte ein Gewicht ein!");
                    return;
                }

                String numberWeight = eTxtNumber.getText().toString();
                if (numberWeight == null || numberWeight == "") {
                    eTxtNumber.setError("Geben Sie bitte ein Gewicht ein!");
                    return;
                }

                float numWeight = 0;
                try {
                    numWeight = Float.parseFloat(numberWeight);
                } catch (Exception e) {
                    eTxtNumber.setError("Geben Sie ein korrektes Gewicht ein!");
                    return;
                }

                if (numWeight < 0) {
                    eTxtNumber.setError("Das Gewicht darf nicht kleiner 0 sein!");
                    return;
                }

                String patientId = Preferences.loadActualPatientID(beobachtung_erfassen_gewicht.this);
                String date = dpObservationWeight.getYear() + "-";
                if (dpObservationWeight.getMonth() + 1 < 10) {
                    date += "0";
                }
                date += (dpObservationWeight.getMonth() + 1) + "-" + dpObservationWeight.getDayOfMonth();

                String fhirServer = Preferences.loadFhirServerUrl(beobachtung_erfassen_gewicht.this);
                String id = null;
                String backendServer = Preferences.loadBackendUrl(beobachtung_erfassen_gewicht.this) + APIEndpoints.WEIGHT_OBSERVATION;

                Log.d(TAG, "Number Steps: " + numberWeight);
                Log.d(TAG, "Patient Id: " + patientId);
                Log.d(TAG, "Date: " + date);
                Log.d(TAG, "Weight: " + numberWeight);
                Log.d(TAG, "FHIR Server: " + fhirServer);
                Log.d(TAG, "Backend URL: " + backendServer);

                HashMap<String, String> data = new HashMap<>();
                data.put("weight", numberWeight);
                data.put("date", date);
                data.put("patientID", patientId);
                data.put("fhirServer", fhirServer);
                data.put("id", id);

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                // Sending data to backend
                JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, backendServer, new JSONObject(data),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String id = "";
                                try {
                                    id = response.getString("id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Log.i(TAG, "onResponse: success, id: "+ id);
                                Toast.makeText(getApplicationContext(), "Beobachtung mit ID " + id + " erfolgreich erstellt!" , duration).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i(TAG, "onResponse: " + error.getMessage());
                                String message = "Fehler beim abspeichern: " + error.getMessage();
                                Toast.makeText(getApplicationContext(), message , duration).show();
                            }
                        }
                ){

                };
                requestQueue.add(jsonobj);
            }
        });
    }
}