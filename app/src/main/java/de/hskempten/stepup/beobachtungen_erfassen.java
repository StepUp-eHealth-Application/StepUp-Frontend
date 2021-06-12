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

public class beobachtungen_erfassen extends AppCompatActivity {

    private final static String TAG = "beobachtungen_erfassen";
    private final static int duration = Toast.LENGTH_SHORT;

    EditText eTxtNumber;
    DatePicker dpObservationSteps;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beobachtungen_erfassen);

        findViews();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberSteps = eTxtNumber.getText().toString();
                if (numberSteps == null || numberSteps == "") {
                    eTxtNumber.setError("Geben Sie bitte Schritte ein!");
                    return;
                }

                int numSteps = 0;
                try {
                    numSteps = Integer.parseInt(numberSteps);
                } catch (Exception e) {
                    eTxtNumber.setError("Geben Sie eine Nummer ein!");
                    return;
                }

                if (numSteps < 0) {
                    eTxtNumber.setError("Anzahl Schritte dürfen nicht kleiner 0 sein!");
                    return;
                }

                String patientId = Preferences.loadActualPatientID(beobachtungen_erfassen.this);
                String date = dpObservationSteps.getYear() + "-";
                if (dpObservationSteps.getMonth() + 1 < 10) {
                    date += "0";
                }
                date += (dpObservationSteps.getMonth() + 1) + "-" + dpObservationSteps.getDayOfMonth();

                String fhirServer = Preferences.loadFhirServerUrl(beobachtungen_erfassen.this);
                String id = null;
                String backendServer = Preferences.loadBackendUrl(beobachtungen_erfassen.this) + APIEndpoints.STEPS_OBSERVATION;

                Log.d(TAG, "Number Steps: " + numberSteps);
                Log.d(TAG, "Patient Id: " + patientId);
                Log.d(TAG, "Date: " + date);
                Log.d(TAG, "Number Steps: " + numberSteps);
                Log.d(TAG, "FHIR Server: " + fhirServer);
                Log.d(TAG, "Backend URL: " + backendServer);

                HashMap<String, String> data = new HashMap<>();
                data.put("steps", numberSteps);
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

    private void findViews() {
        eTxtNumber = findViewById(R.id.eTxtNumberSteps);
        dpObservationSteps = findViewById(R.id.dpObservationSteps);
        save = findViewById(R.id.btnSaveObservationSteps);
    }
}