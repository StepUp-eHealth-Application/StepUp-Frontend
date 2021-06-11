package de.hskempten.stepup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hskempten.stepup.helpers.APIEndpoints;
import de.hskempten.stepup.preferences.Preferences;

public class SearchPatientActivity extends AppCompatActivity {

    private static final String TAG = "SearchPatient";

    SearchView searchView;
    ListView listView;
    ArrayList<String> listName;
    ArrayList<String> listId;
    ArrayAdapter<String> adapter;

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

        String backendUrl = getBackendUrl();
        setupQueue();
        getPatientsFromBackend(backendUrl);

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

    private void setupQueue() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    private String getBackendUrl() {
        String backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.PATIENT;
        Log.d(TAG, "Backend URL: " + backendUrl);
        return backendUrl;
    }

    private void getPatientsFromBackend(String backendUrl) {
        JsonArrayRequest jsonarr = new JsonArrayRequest(Request.Method.GET, backendUrl, new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0; i<response.length(); i++) {
                                JSONObject jsonobj = response.getJSONObject(i);

                                // Get Patient ID
                                String patientId = jsonobj.getString("id");

                                // Get name without catch on missing data
                                String firstName = "[firstName]";
                                String lastName = "[lastName]";
                                if(jsonobj.has("firstName") && !jsonobj.isNull("firstName")) {
                                    firstName = jsonobj.getString("firstName");
                                }
                                if(jsonobj.has("lastName") && !jsonobj.isNull("lastName")) {
                                    lastName = jsonobj.getString("lastName");
                                }

                                // Make new list entry
                                newListEntry(patientId, firstName, lastName);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onResponse: success");
                        createList();
                        Toast.makeText(getApplicationContext(), "Verfügbare Patienten erfolgreich geladen!", duration).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onResponse: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Patienten konnten nicht geladen werden!", duration).show();
                    }
                });
        requestQueue.add(jsonarr);
    }

    private void newListEntry(String patientId, String firstName, String lastName) {
        listId.add(patientId);
        listName.add(firstName + " " + lastName);
    }

    private void createList() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listName);
        listView.setAdapter(adapter);
    }
}