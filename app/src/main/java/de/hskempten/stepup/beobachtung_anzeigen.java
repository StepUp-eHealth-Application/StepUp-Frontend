package de.hskempten.stepup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hskempten.stepup.helpers.APIEndpoints;
import de.hskempten.stepup.helpers.ActivityInterfaceKeys;
import de.hskempten.stepup.preferences.Preferences;

public class beobachtung_anzeigen extends AppCompatActivity {

    private static final String TAG = "beobachtungen_anzeigen";

    String patientId;
    TextView txtTitle;
    FloatingActionButton btnNew;
    ArrayList<DataModelObservation> arrayList;
    ListView listView;
    String goalType;
    private static ObservationAdapter adapter;

    RequestQueue requestQueue;

    int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beobachtung_anzeigen);
        Intent intent = getIntent();
        String goalId = intent.getStringExtra(ActivityInterfaceKeys.HEALTH_GOAL_ID);
        goalType = intent.getStringExtra(ActivityInterfaceKeys.HEALTH_GOAL_TYPE);
        setupQueue();

        // getting patient
        patientId = Preferences.loadSelectedPatientID(getApplicationContext());
        if (patientId == null || patientId == "") {
            patientId = Preferences.loadActualPatientID(getApplicationContext());
        }
        txtTitle = findViewById(R.id.txtPatientName);
        String backendUrlName = getBackendUrlName();
        getPatientNameFromBackend(backendUrlName);
        String backendUrlObservationSteps = getBackendUrlObservationSteps();
        String backendUrlObservationWeight = getBackendUrlObservationWeight();
        if(goalType == "steps") {
            getPatientObservationsFromBackend(backendUrlObservationSteps);
        } else {
            getPatientObservationsFromBackend(backendUrlObservationWeight);
        }

        // getting patient goals
        listView = (ListView) findViewById(R.id.lstObservations);
        arrayList = new ArrayList<>();
        adapter = new ObservationAdapter(arrayList, getApplicationContext());
        listView.setAdapter(adapter);
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
                        String firstName = null;
                        String lastName = null;
                        try {
                            firstName = response.getString("firstName");
                            lastName = response.getString("lastName");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onResponse: success");
                        changeName(firstName, lastName);
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

    private String getBackendUrlObservationSteps() {
        String backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.STEPS_OBSERVATION_PATIENT + patientId;
        Log.d(TAG, "Backend URL: " + backendUrl);
        return backendUrl;
    }

    private String getBackendUrlObservationWeight() {
        String backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.WEIGHT_OBSERVATION_PATIENT + patientId;
        Log.d(TAG, "Backend URL: " + backendUrl);
        return backendUrl;
    }

    private void getPatientObservationsFromBackend(String backendUrlObservations) {
        // get all observations
        JsonArrayRequest obsArr = new JsonArrayRequest(Request.Method.GET, backendUrlObservations, new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Get all goals
                            for(int i=0; i<response.length(); i++) {
                                JSONObject obsObj = response.getJSONObject(i);

                                // Get ID
                                String id = obsObj.getString("id");
                                String device = obsObj.getString("device");
                                String date = obsObj.getString("date");
                                String accomplished = obsObj.getString(goalType);

                                // create new widget
                                addListItem(id, device, date, accomplished);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onResponse: success");
                        Toast.makeText(getApplicationContext(), "Beobachtungen wurden erfolgreich geladen.", duration).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onResponse: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Gesundheitsziele 'Gewicht' konnten nicht geladen werden!", duration).show();
                    }
                });
        requestQueue.add(obsArr);
    }

    private void changeName(String firstName, String lastName) {
        String fullName = firstName + ' ' + lastName;
        txtTitle.setText(fullName);
    }

    private void addListItem(String id, String device, String date, String accomplished) {
        arrayList.add(new DataModelObservation(id, device, date, accomplished));
    }
}