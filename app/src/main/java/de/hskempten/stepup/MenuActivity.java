package de.hskempten.stepup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivity";

    TextView txtTitle;
    LinearLayout viewSettings;
    ImageButton btnSettings;
    FloatingActionButton btnNew;
    String patientId;
    ArrayList<DataModelGoal> arrayList;
    ListView listView;
    private static GoalAdapter adapter;
    String backendUrlGoalSteps;
    String backendUrlGoalWeight;

    String goalType;
    String goalId;

    RequestQueue requestQueue;

    int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setupQueue();

        // getting patient
        patientId = Preferences.loadSelectedPatientID(getApplicationContext());
        if (patientId == null || patientId == "") {
            patientId = Preferences.loadActualPatientID(getApplicationContext());
        }
        txtTitle = findViewById(R.id.txtPatientName);
        String backendUrlName = getBackendUrlName();
        getPatientNameFromBackend(backendUrlName);
        backendUrlGoalSteps = getBackendUrlGoalSteps();
        backendUrlGoalWeight = getBackendUrlGoalWeight();

        // getting patient goals
        listView = (ListView) findViewById(R.id.lstGoals);
        arrayList = new ArrayList<>();
        adapter = new GoalAdapter(arrayList, getApplicationContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataModelGoal dataModelGoal = arrayList.get(position);
                goalId = dataModelGoal.getId();
                goalType = dataModelGoal.getType();
                //Alert Dialog Button, Choose beetween Steps and Weight Helath Goal
                AlertDialog.Builder builderEdit_Observations = new AlertDialog.Builder(MenuActivity.this);
                builderEdit_Observations.setTitle("Aktion Auswählen")
                        .setPositiveButton("Beobachtungen anzeigen", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getApplicationContext(), beobachtung_anzeigen.class);
                                intent.putExtra(ActivityInterfaceKeys.HEALTH_GOAL_ID, goalId);
                                intent.putExtra(ActivityInterfaceKeys.HEALTH_GOAL_TYPE, goalType);
                                MenuActivity.this.startActivity(intent);
                            }
                        })
                        .setNegativeButton("Gesundheitsziel bearbeiten", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getApplicationContext(), gesundheitszieleSetztenAendern.class);
                                intent.putExtra(ActivityInterfaceKeys.HEALTH_GOAL_ID, goalId);
                                intent.putExtra(ActivityInterfaceKeys.HEALTH_GOAL_TYPE, goalType + "Goal");
                                intent.putExtra("previousPage", "aendern");
                                MenuActivity.this.startActivity(intent);
                            }
                        });
                AlertDialog dialogGoalSelected = builderEdit_Observations.create();
                dialogGoalSelected.show();
            }
        });

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
                Intent intent = new Intent(MenuActivity.this, DisplayPatientDataActivity.class);
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
        //Alert Dialog Button, Choose beetween Steps and Weight Helath Goal
        AlertDialog.Builder builderWeight_Steps = new AlertDialog.Builder(MenuActivity.this);
        builderWeight_Steps.setTitle("Neues Gesundheitsziel erstellen")
                .setPositiveButton("Gesundheitsziel Schritte", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MenuActivity.this, gesundheitszieleSetztenAendern.class);
                        intent.putExtra(ActivityInterfaceKeys.HEALTH_GOAL_TYPE, "stepsGoal");
                        intent.putExtra("previousPage", "neu");
                        MenuActivity.this.startActivity(intent);
                    }
                })
                .setNegativeButton("Gesundheitsziel Gewicht", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MenuActivity.this, gesundheitszieleSetztenAendern.class);
                        intent.putExtra(ActivityInterfaceKeys.HEALTH_GOAL_TYPE, "weightGoal");
                        intent.putExtra("previousPage", "neu");
                        MenuActivity.this.startActivity(intent);
                    }
                });

        btnNew = findViewById(R.id.btnNew);
        Log.d(TAG, "onCreate: " + btnNew);
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialogHealthGoal = builderWeight_Steps.create();
                dialogHealthGoal.show();
            }
        });

        Button addDevice = findViewById(R.id.btnAddDevice);
        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, DeviceCreationActivity.class);
                MenuActivity.this.startActivity(intent);
            }
        });
        // Opening Create Profile Settings when "Profi erstellen" Button clicked
        Button btnCreatePatient = findViewById(R.id.btnCreatePatient);
        btnCreatePatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, PatientDataActivity.class);
                MenuActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        arrayList.clear();
        getPatientGoalsFromBackend(backendUrlGoalSteps, backendUrlGoalWeight);
        adapter.notifyDataSetChanged();
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
                        String firstName = null;
                        String lastName = null;
                        try {
                            firstName = response.getString("firstName");
                            lastName = response.getString("lastName");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "loadingPatientName: success");
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

    private String getBackendUrlGoalSteps() {
        String backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.STEPS_GOAL_PATIENT + patientId;
        Log.d(TAG, "Backend URL: " + backendUrl);
        return backendUrl;
    }

    private String getBackendUrlGoalWeight() {
        String backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.WEIGHT_GOAL_PATIENT + patientId;
        Log.d(TAG, "Backend URL: " + backendUrl);
        return backendUrl;
    }

    private void getPatientGoalsFromBackend(String backendUrlGoalSteps, String backendUrlGoalWeight) {
        // get all step goals
        JsonArrayRequest stepsArr = new JsonArrayRequest(Request.Method.GET, backendUrlGoalSteps, new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0; i<response.length(); i++) {
                                JSONObject stepsObj = response.getJSONObject(i);

                                // Get ID
                                String id = stepsObj.getString("id");
                                String description = stepsObj.getString("description");
                                String dueDate = stepsObj.getString("dueDate");
                                String stepsGoal = stepsObj.getString("stepsGoal");
                                String type = "steps";
                                Log.d(TAG, "Steps loop, iteration: " + i);

                                // create new widget
                                addListItem(id, description, dueDate, stepsGoal, type);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "stepGoals: " + response.length());
                        Toast.makeText(getApplicationContext(), "Gesundheitsziele 'Schritte' wurden erfolgreich geladen!", duration).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onResponse: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Gesundheitsziele 'Schritte' konnten nicht geladen werden!", duration).show();
                    }
                });
        requestQueue.add(stepsArr);
        // get all weight goals
        JsonArrayRequest weightArr = new JsonArrayRequest(Request.Method.GET, backendUrlGoalWeight, new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Get all goals
                            for(int i=0; i<response.length(); i++) {
                                JSONObject stepsObj = response.getJSONObject(i);

                                // Get ID
                                String id = stepsObj.getString("id");
                                String description = stepsObj.getString("description");
                                String dueDate = stepsObj.getString("dueDate");
                                String weightGoal = stepsObj.getString("weightGoal") + " kg";
                                String type = "weight";
                                Log.d(TAG, "Weight loop, iteration: " + i);

                                // create new widget
                                addListItem(id, description, dueDate, weightGoal, type);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "weightGoals: " + response.length());
                        Toast.makeText(getApplicationContext(), "Gesundheitsziele 'Gewicht' wurden erfolgreich geladen!", duration).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onResponse: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Gesundheitsziele 'Gewicht' konnten nicht geladen werden!", duration).show();
                    }
                });
        requestQueue.add(weightArr);
    }

    private void changeName(String firstName, String lastName) {
        String fullName = firstName + ' ' + lastName;
        txtTitle.setText(fullName);
    }

    private void addListItem(String id, String description, String dueDate, String goal, String type) {
        arrayList.add(new DataModelGoal(id, description, dueDate, goal, type));
        adapter.notifyDataSetChanged();
    }
}
