package de.hskempten.stepup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
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
import java.util.HashMap;

import de.hskempten.stepup.helpers.APIEndpoints;
import de.hskempten.stepup.preferences.Preferences;

public class SearchPatientActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SearchPatientActivity";

    SearchView searchView;
    ListView listView;
    ArrayList<String> listName;
    ArrayList<String> listId;
    ArrayAdapter<String> adapter;
    Spinner genderSpinner;
    Button searchByGender;

    RequestQueue requestQueue;
    int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_patient);
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);
        listName = new ArrayList<>();
        listId = new ArrayList<>();

        // filling gender spinner
        genderSpinner = (Spinner) findViewById(R.id.spinnerPatientGender);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        searchByGender = findViewById(R.id.btnSearchByGender);
        searchByGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gender = genderSpinner.getSelectedItem().toString();
                String backendServer = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.PATIENT + "gender/" + gender;

                Toast.makeText(getApplicationContext(), "Filtere...", Toast.LENGTH_SHORT).show();

                JsonArrayRequest jsonobj = new JsonArrayRequest(Request.Method.GET, backendServer, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.d(LOG_TAG, "onResponse: success");

                                listName.clear();
                                listId.clear();

                                // Placeholder fill list
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject patientObj = response.getJSONObject(i);

                                        String firstName = patientObj.getString("firstName");
                                        String lastName = patientObj.getString("lastName");
                                        String id = patientObj.getString("id");

                                        listName.add(firstName + " " + lastName);
                                        listId.add(id);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                adapter.notifyDataSetChanged();

                                Log.d(LOG_TAG, "onClick: search by gender finished");
                                Toast.makeText(getApplicationContext(), "Nach Geschlecht '" + gender + "' gefiltert", Toast.LENGTH_LONG).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(LOG_TAG, "onResponse: " + error.getMessage());

                                Toast.makeText(getApplicationContext(), "Fehler beim Laden aller Patienten", Toast.LENGTH_LONG).show();
                            }
                        }
                ){

                };
                requestQueue.add(jsonobj);
            }
        });

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        String backendServer = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.PATIENT;
        getPatients(backendServer);
    }

    private void getPatients(String backendServer) {
        // Getting all Patients
        Toast.makeText(getApplicationContext(), "Lade alle Patienten. Einen Moment bitte...", Toast.LENGTH_SHORT).show();
        JsonArrayRequest jsonobj = new JsonArrayRequest(Request.Method.GET, backendServer, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(LOG_TAG, "onResponse: success");

                        Toast.makeText(getApplicationContext(), "Alle Patienten erfolgreich geladen!", Toast.LENGTH_LONG).show();

                        // Placeholder fill list
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject patientObj = response.getJSONObject(i);

                                String firstName = patientObj.getString("firstName");
                                String lastName = patientObj.getString("lastName");
                                String id = patientObj.getString("id");

                                listName.add(firstName + " " + lastName);
                                listId.add(id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listName);
                        listView.setAdapter(adapter);

                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                if(listName.contains(query)){
                                    adapter.getFilter().filter(query);
                                }else{
                                    Toast.makeText(SearchPatientActivity.this, "Keine Übereinstimmung gefunden.",Toast.LENGTH_LONG).show();
                                }
                                return false;
                            }
                            @Override
                            public boolean onQueryTextChange(String newText) {
                                adapter.getFilter().filter(newText);
                                return false;
                            }
                        });

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                Intent intent = new Intent(SearchPatientActivity.this, MenuLeActivity.class);
                                Preferences.saveSelectedPatientID(listId.get(position), getApplicationContext());
                                startActivity(intent);
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "onResponse: " + error.getMessage());

                        Toast.makeText(getApplicationContext(), "Fehler beim Laden aller Patienten", Toast.LENGTH_LONG).show();
                    }
                }
        ){

        };
        requestQueue.add(jsonobj);
    }
}