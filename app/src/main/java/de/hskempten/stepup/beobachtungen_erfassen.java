package de.hskempten.stepup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hskempten.stepup.helpers.APIEndpoints;
import de.hskempten.stepup.preferences.Preferences;

public class beobachtungen_erfassen extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final static String TAG = "beobachtungen_erfassen";
    private final static int duration = Toast.LENGTH_SHORT;

    EditText eTxtNumber;
    DatePicker dpObservationSteps;
    Button save;
    Spinner deviceSpinner;
    Spinner goalSpinner;

    RequestQueue requestQueue;

    List<String> deviceIds;
    List<String> deviceNames;
    String deviceId;

    List<String> goalIds;
    List<String> goalValues;
    String goalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beobachtungen_erfassen);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        findViews();
        String patientId = Preferences.loadActualPatientID(getApplicationContext());
        Log.d(TAG, "onCreate: Patient id: " + patientId);

        initDeviceSpinner();
        initButtonOnClick();
        initGoalSpinner();
    }

    private void initButtonOnClick() {
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
                Log.d(TAG, "Goal ID: " + goalId);

                HashMap<String, String> data = new HashMap<>();
                data.put("steps", numberSteps);
                data.put("date", date);
                data.put("patientID", patientId);
                data.put("fhirServer", fhirServer);
                data.put("id", id);
                data.put("deviceID", deviceId);
                data.put("goalID", goalId);

                requestQueue = Volley.newRequestQueue(getApplicationContext());

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

    private void initDeviceSpinner() {
        String backendServer = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.DEVICE;
        // Sending data to backend
        JsonArrayRequest jsonobj = new JsonArrayRequest(Request.Method.GET, backendServer, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        deviceIds = new ArrayList<>();
                        deviceNames = new ArrayList<>();

                        for(int i = 0; i < response.length(); i++){
                            JSONObject jresponse = null;
                            try {
                                jresponse = response.getJSONObject(i);

                                String id = jresponse.getString("id");
                                String name = jresponse.getString("name");

                                if (deviceId == null || deviceId.isEmpty()) {
                                    deviceId = id;
                                }

                                deviceIds.add(id);
                                deviceNames.add(name);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                getApplicationContext(), android.R.layout.simple_spinner_item, deviceNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        deviceSpinner.setAdapter(adapter);

                        Toast.makeText(getApplicationContext(), "Geräte erfolgreich geladen!" , duration).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onResponse: " + error.getMessage());
                        String message = "Geräte konnten nicht geladen werden: " + error.getMessage();
                        Toast.makeText(getApplicationContext(), message , duration).show();
                    }
                }
        ){

        };
        requestQueue.add(jsonobj);

        deviceSpinner.setOnItemSelectedListener(this);
    }

    private void initGoalSpinner() {
        String backendServer = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.STEPS_GOAL + "patient/" + Preferences.loadActualPatientID(getApplicationContext());

        // Sending data to backend
        JsonArrayRequest jsonobj = new JsonArrayRequest(Request.Method.GET, backendServer, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        goalIds = new ArrayList<>();
                        goalValues = new ArrayList<>();

                        for(int i = 0; i < response.length(); i++){
                            JSONObject jresponse = null;
                            try {
                                jresponse = response.getJSONObject(i);

                                String id = jresponse.getString("id");
                                String value = jresponse.getString("stepsGoal");
                                String description = jresponse.getString("description");
                                String dueDate = jresponse.getString("dueDate");

                                if (dueDate != null && !dueDate.isEmpty()) {
                                    dueDate = dueDate.split("T")[0];
                                }

                                if (deviceId == null || deviceId.isEmpty()) {
                                    deviceId = id;
                                }

                                goalIds.add(id);
                                goalValues.add(description + ": " + value + " (" + dueDate + ")");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                getApplicationContext(), android.R.layout.simple_spinner_item, goalValues);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        goalSpinner.setAdapter(adapter);
                        goalSpinner.setOnItemSelectedListener(beobachtungen_erfassen.this);

                        Toast.makeText(getApplicationContext(), "Gesundheitsziele erfolgreich geladen!" , duration).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onResponse: " + error.getMessage());
                        String message = "Gesundheitsziele konnten nicht geladen werden: " + error.getMessage();
                        Toast.makeText(getApplicationContext(), message , duration).show();
                    }
                }
        ){

        };
        requestQueue.add(jsonobj);
    }

    private void findViews() {
        eTxtNumber = findViewById(R.id.eTxtNumberSteps);
        dpObservationSteps = findViewById(R.id.dpObservationSteps);
        save = findViewById(R.id.btnSaveObservationSteps);
        deviceSpinner = findViewById(R.id.spinnerDeviceSteps);
        goalSpinner = findViewById(R.id.spinnerStepsObservation);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinnerDeviceSteps:
                deviceId = deviceIds.get(position);
                break;
            case R.id.spinnerStepsObservation:
                try {
                    goalId = goalIds.get(position);
                } catch (IndexOutOfBoundsException exception) {
                    Log.d(TAG, "onItemSelected: Index out of bounce in goal IDs");
                }
                break;
            default:
                Log.d(TAG, "onItemSelected: Error default");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}