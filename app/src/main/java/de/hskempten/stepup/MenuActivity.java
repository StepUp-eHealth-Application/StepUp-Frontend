package de.hskempten.stepup;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hskempten.stepup.helpers.APIEndpoints;
import de.hskempten.stepup.preferences.Preferences;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivity";

    TextView txtTitle;
    LinearLayout viewSettings;
    ImageButton btnSettings;
    String patientId;
    ArrayList<DataModelGoal> arrayList;
    ListView listView;
    private static GoalAdapter adapter;

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

        // getting patient goals
        listView = (ListView) findViewById(R.id.lstGoals);
        arrayList = new ArrayList<>();
        addListItem("id","des", "date", "goal", "acc");
        adapter = new GoalAdapter(arrayList, getApplicationContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataModelGoal dataModelGoal = arrayList.get(position);
                Intent intent = new Intent(getApplicationContext(), beobachtung_anzeigen.class);
                Bundle b = null;
                b.putString("HEALTH_GOAL_ID", dataModelGoal.getId());
                intent.putExtras(b);
                MenuActivity.this.startActivity(intent);
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

    private String getBackendUrlGoalSteps() {
        String backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + "api/v1/goal/steps/" + patientId;
        Log.d(TAG, "Backend URL: " + backendUrl);
        return backendUrl;
    }

    private String getBackendUrlGoalWeight() {
        String backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + "api/v1/goal/weight/" + patientId;
        Log.d(TAG, "Backend URL: " + backendUrl);
        return backendUrl;
    }

    private void getPatientGoalsFromBackend(String backendUrlGoalSteps, String backendUrlGoalWeight) {
        JsonArrayRequest jsonarr = new JsonArrayRequest(Request.Method.GET, backendUrlGoalSteps, new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Get all goals
                            for(int i=0; i<response.length(); i++) {
                                JSONObject jsonobj = response.getJSONObject(i);

                                // Get ID
                                String id = jsonobj.getString("id");
                                String description = jsonobj.getString("description");
                                String dueDate = jsonobj.getString("dueDate");
                                String stepsGoal = jsonobj.getString("stepsGoal");

                                // calculate accomplishment
                                String accomplished = "foo";

                                // create new widget
                                addListItem(id, description, dueDate, stepsGoal, accomplished);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onResponse: success");
                        //exe
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
        requestQueue.add(jsonarr);
    }

    private void changeName(String firstName, String lastName) {
        String fullName = firstName + ' ' + lastName;
        txtTitle.setText(fullName);
    }

    private void addListItem(String id, String description, String dueDate, String goal, String accomplished) {
        arrayList.add(new DataModelGoal(id, description, dueDate, goal, accomplished));
    }
}
