package de.hskempten.stepup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    TextView txtGoalname;
    TextView txtGoalvalue;
    FloatingActionButton btnNew;
    ArrayList<DataModelObservation> arrayList;
    ListView listView;
    String goalType = "";
    String goalId = "";
    String goalDate = "";
    private static ObservationAdapter adapter;
    String backendUrlName;
    String backendUrlGoal;
    String backendUrlObservationSteps;
    String backendUrlObservationWeight;

    RequestQueue requestQueue;

    int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beobachtung_anzeigen);
        Intent intent = getIntent();
        if(getIntent().hasExtra(ActivityInterfaceKeys.HEALTH_GOAL_ID)) goalId = intent.getStringExtra(ActivityInterfaceKeys.HEALTH_GOAL_ID);
        if(getIntent().hasExtra(ActivityInterfaceKeys.HEALTH_GOAL_TYPE)) goalType = intent.getStringExtra(ActivityInterfaceKeys.HEALTH_GOAL_TYPE);
        if(getIntent().hasExtra(ActivityInterfaceKeys.HEALTH_GOAL_DATE)) goalDate = intent.getStringExtra(ActivityInterfaceKeys.HEALTH_GOAL_DATE);
        setupQueue();

        // getting patient
        patientId = Preferences.loadSelectedPatientID(getApplicationContext());
        if (patientId == null || patientId == "") {
            patientId = Preferences.loadActualPatientID(getApplicationContext());
        }
        txtTitle = findViewById(R.id.txtPatientName);
        txtGoalname = findViewById(R.id.txtGoalname);
        txtGoalvalue = findViewById(R.id.txtGoalvalue);
        backendUrlName = getBackendUrlName();
        backendUrlGoal = getBackendUrlGoal();
        backendUrlObservationSteps = getBackendUrlObservationSteps();
        backendUrlObservationWeight = getBackendUrlObservationWeight();
        getPatientNameFromBackend(backendUrlName);
        getGoalFromBackend(backendUrlGoal);
        // getting patient observations
        listView = (ListView) findViewById(R.id.lstObservations);
        arrayList = new ArrayList<>();
        adapter = new ObservationAdapter(arrayList, getApplicationContext());
        listView.setAdapter(adapter);

        btnNew = findViewById(R.id.btnNew);
        Log.d(TAG, "onCreate: " + btnNew);
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(beobachtung_anzeigen.this, ChooseObservationType.class);
                beobachtung_anzeigen.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        arrayList.clear();
        switch (goalType) {
            case "steps":
                getPatientObservationsFromBackend(backendUrlObservationSteps);
                break;
            case "weight":
                getPatientObservationsFromBackend(backendUrlObservationWeight);
                break;
        }
        adapter.notifyDataSetChanged();
    }

    private void setupQueue() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    private String getBackendUrlName() {
        String backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.PATIENT + patientId;
        Log.d(TAG, "Backend URL: " + backendUrl);
        return backendUrl;
    }

    private String getBackendUrlGoal() {
        String backendUrl = "";
        if (goalType.equals("steps")) backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.STEPS_GOAL + goalId;
        else if (goalType.equals("weight")) backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.WEIGHT_GOAL + goalId;
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
        String backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.STEPS_OBSERVATION + "goal/" + goalId + "/patient/" + patientId;
        Log.d(TAG, "Backend URL: " + backendUrl);
        return backendUrl;
    }

    private String getBackendUrlObservationWeight() {
        String backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.WEIGHT_OBSERVATION + "goal/" + goalId + "/patient/" + patientId;
        Log.d(TAG, "Backend URL: " + backendUrl);
        return backendUrl;
    }

    private void getGoalFromBackend(String BackendUrl) {
        // get Goal Name
        JsonObjectRequest goalObj = new JsonObjectRequest(Request.Method.GET, BackendUrl, new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            txtGoalname.setText(response.getString("description"));
                            Log.d(TAG, "goalType: " + goalType);
                            if (goalType.equals("steps")) txtGoalvalue.setText(response.getString("stepsGoal"));
                            else if (goalType.equals("weight")) txtGoalvalue.setText(response.getString("weightGoal") + " kg");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), "Gesundheitsziel wurde erfolgreich geladen.", duration).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Gesundheitsziel konnte nicht geladen werden.", duration).show();
                    }
                });
        requestQueue.add(goalObj);
    }

    private void getPatientObservationsFromBackend(String backendUrlObservations) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("patientID", patientId);
            obj.put("date", goalDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // get all observations
        JsonArrayRequest obsArr = new JsonArrayRequest(Request.Method.GET, backendUrlObservations, new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Get all observations
                            for(int i=0; i<response.length(); i++) {
                                JSONObject obsObj = response.getJSONObject(i);

                                // Get ID
                                String id = obsObj.getString("id");
                                String device = "";
                                if(obsObj.has("device") && !obsObj.isNull("device")) {
                                    device = obsObj.getString("device");
                                }
                                String date = obsObj.getString("date");
                                String accomplished = "";
                                if (goalType.equals("steps")) accomplished = obsObj.getString("steps");
                                else if (goalType.equals("weight")) accomplished = obsObj.getString("weight");

                                // create new widget
                                addListItem(id, device, date, accomplished);
                                Log.d(TAG, "added list item");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onResponse: success, url: " + backendUrlObservations);
                        Toast.makeText(getApplicationContext(), "Beobachtungen wurden erfolgreich geladen.", duration).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onResponse: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Beobachtungen konnten nicht geladen werden.", duration).show();
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
        adapter.notifyDataSetChanged();
    }
}