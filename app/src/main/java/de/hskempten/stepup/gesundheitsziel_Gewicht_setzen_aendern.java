package de.hskempten.stepup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

import java.util.HashMap;


import org.json.JSONException;
import org.json.JSONObject;

import de.hskempten.stepup.preferences.Preferences;


public class gesundheitsziel_Gewicht_setzen_aendern extends AppCompatActivity {
    private static final String LOG_TAG = "HealthGoalsWeight";
    public String previousPage = "aendern"; //leer lassen und von vorheriger View beschreiben lassen
    String backendUrl;

    // TextView healthGoalName;
    DatePicker dateHealthGoal;
    EditText healthGoalData;

    String stringHealthGoalName;
    String stringDateHealthGoal;
    String stringHealthGoalData;

    String patiendId;
    String goalId;

    RequestQueue requestQueue;

    int duration = Toast.LENGTH_SHORT;

    String goalType = "weightGoal";
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesundheitsziel__gewicht_setzen_aendern);



        //Spinner
        Spinner einheitSpinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter <String> einheitAdapter = new ArrayAdapter<String>(gesundheitsziel_Gewicht_setzen_aendern.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.einheiten));

        einheitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        einheitSpinner.setAdapter(einheitAdapter);

        findViews();
        setupQueue();

        if(previousPage == "aendern"){
            backendUrl = getBackendUrl(previousPage);
            getHealthGoalDataFromBackend(backendUrl);
        }

        buttonSave = (Button) findViewById(R.id.btnSaveWeightObservation);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // String backendUrl = getBackendUrl(previousPage);
                //  setHealthGoalToBackend(backendUrl);


                if(previousPage == "aendern"){
                    previousPage = "put";
                    backendUrl = getBackendUrl(previousPage);
                    putHealthGoalDataToBackend(backendUrl);
                }
                else if(previousPage == "neu"){
                    backendUrl = getBackendUrl(previousPage);
                    setHealthGoalToBackend(backendUrl);
                }
            }
        });

    }
    private String getBackendUrl(String previousPage){
        String backendUrl = "";
        //get PatientId
        patiendId = Preferences.loadActualPatientID(getApplicationContext());
        //goalId = HEALTH_GOAL_ID;
        goalId = "2181791";

        //get correct Url
        if(previousPage == "aendern"){
            backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + "api/v1/goal/weight/" + goalId;
        }else if(previousPage == "neu"){
            backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + "api/v1/goal/weight";
        }else if (previousPage == "put"){
            backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + "api/v1/goal/weight/" + goalId + "/";
        }
        Log.d(LOG_TAG, "Backend URL: " + backendUrl);
        return backendUrl;
    }
    private void setupQueue(){
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }
    private void getHealthGoalDataFromBackend(String backendUrl){
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.GET, backendUrl, new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //JSONObject responseObj = response.getJSONObject(i);

                            //healthGoalName.setText(responseObj.getString("description"));

                            String dateString = response.getString("dueDate");
                            //parse dateString
                            String[] splittedDate = dateString.split("-");
                            int  year = Integer.parseInt(splittedDate[0]);
                            int month =  Integer.parseInt(splittedDate[1]);
                            //get Day out of last String
                            String[] splittedDay = splittedDate[2].split("T");
                            int day =  Integer.parseInt(splittedDay[0]);

                            dateHealthGoal.updateDate(year, month-1, day);

                            healthGoalData.setText(response.getString(goalType));

                        }catch(JSONException e){
                            e.printStackTrace();
                        }


                        Log.d(LOG_TAG, "onResponse: getHealthGoals success");
                        Toast.makeText(getApplicationContext(), "Gesundheitsziel erfolgreich geladen!", duration).show();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG, "onErrorResponse: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Gesundheitsziel konnte nicht geladen werden", duration).show();
                    }
                }
        ){

        };
        requestQueue.add(jsonobj);
    }
    private void setHealthGoalToBackend(String backendUrl){
        //stringHealthGoalName = healthGoalName.getText().toString();
        stringHealthGoalData = healthGoalData.getText().toString();

        //get dateString out of Date Picker
        String  day = String.valueOf(dateHealthGoal.getDayOfMonth());
        if(day.length() == 1){
            day = "0" + day;
        }
        String month = String.valueOf(dateHealthGoal.getMonth()+1);
        if(month.length() == 1){
            month = "0" + month;
        }
        String year = String.valueOf(dateHealthGoal.getYear());

        //create dateString
        stringDateHealthGoal = year +  "-" + month +  "-" + day + "T00:00:00Z";
        Log.d(LOG_TAG, "TEST:  " + stringDateHealthGoal +  "_" + stringHealthGoalData +  "_"  + "_" + patiendId);

        //prepare HashMap data for POST-Request
        HashMap<String, String> data = new HashMap<>();
        //data.put("description", stringHealthGoalName);
        data.put("patientId", patiendId);
        data.put("dueDate", stringDateHealthGoal);
        data.put(goalType, stringHealthGoalData);


        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, backendUrl, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String id = "";
                        try {
                            id = response.getString("id");
                        }catch(JSONException e){
                            e.printStackTrace();
                        }

                        Log.d(LOG_TAG, "onResponse: setHealthGoals success");
                        Toast.makeText(getApplicationContext(), "Gesundheitsziel erfolgreich gespeichert! ID: " + id, duration).show();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG, "onErrorResponse: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Gesundheitsziel konnten nicht gespeichert werden!", duration).show();
                    }
                }
        ){

        };
        requestQueue.add(jsonobj);
    }
    private void putHealthGoalDataToBackend(String backendUrl){
        stringHealthGoalData = healthGoalData.getText().toString();
        //get dateString out of Date Picker
        String  day = String.valueOf(dateHealthGoal.getDayOfMonth());
        if(day.length() == 1){
            day = "0" + day;
        }
        String month = String.valueOf(dateHealthGoal.getMonth()+1);
        if(month.length() == 1){
            month = "0" + month;
        }
        String year = String.valueOf(dateHealthGoal.getYear());

        //create dateString
        stringDateHealthGoal = year +  "-" + month +  "-" + day + "T00:00:00Z";

        //prepare HashMap data for POST-Request
        HashMap<String, String> data = new HashMap<>();
        data.put("description", "Gesundheitsziel Schritte");
        data.put("patientId", patiendId);
        data.put("dueDate", stringDateHealthGoal);
        data.put(goalType, stringHealthGoalData);
        data.put("id", goalId);
        Log.d(LOG_TAG, "onResponse: " + stringHealthGoalData + "_" + stringDateHealthGoal);


        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, backendUrl, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String id = "";
                        try {
                            id = response.getString("id");
                        }catch(JSONException e){
                            e.printStackTrace();
                        }


                        Log.d(LOG_TAG, "onResponse: putHealthGoals success");
                        Toast.makeText(getApplicationContext(), "Gesundheitsziel erfolgreich geändert!", duration).show();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG, "onErrorResponse: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Gesundheitsziel konnten nicht geändert werden!", duration).show();
                    }
                }
        ){

        };
        requestQueue.add(putRequest);
    }

    private void findViews(){
        //healthGoalName = (TextView) findViewById(R.id.txtHealthGoalName);
        dateHealthGoal = findViewById(R.id.dateHealthGoal_Weight);
        healthGoalData = findViewById(R.id.eTxtWeightObservation);
    }
}