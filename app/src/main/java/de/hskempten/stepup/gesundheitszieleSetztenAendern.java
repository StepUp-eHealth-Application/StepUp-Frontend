package de.hskempten.stepup;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hskempten.stepup.helpers.ActivityInterfaceKeys;
import de.hskempten.stepup.preferences.Preferences;

public class gesundheitszieleSetztenAendern extends AppCompatActivity {

    private static final String LOG_TAG = "HealthGoals";
    String previousPage;// = "aendern"; //leer lassen und von vorheriger View beschreiben lassen
    String goalType;// = "stepsGoal";//leer lassen und von vorheriger View beschreiben lassen
    String backendUrl;

    TextView healthGoalName;
    TextView healtGoalDataDescription;
    DatePicker dateHealthGoal;
    EditText healthGoalData;

    String stringHealthGoalName;
    String stringDateHealthGoal;
    String stringHealthGoalData;

    String patiendId;
    String goalId;

    RequestQueue requestQueue;

    int duration = Toast.LENGTH_SHORT;


    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesundheitsziele_setzten_aendern);
        //get User Action
        if(getIntent().hasExtra("previousPage")) {
            previousPage = (String) getIntent().getExtras().getString("previousPage");
        }
        if(getIntent().hasExtra(ActivityInterfaceKeys.HEALTH_GOAL_TYPE)) {
            goalType = getIntent().getExtras().getString(ActivityInterfaceKeys.HEALTH_GOAL_TYPE);
        }
        if(getIntent().hasExtra(ActivityInterfaceKeys.HEALTH_GOAL_ID)) {
            goalId = getIntent().getExtras().getString(ActivityInterfaceKeys.HEALTH_GOAL_ID);
        }
        Log.d("gesundheitszielsetzen", goalType);
        //set correct heading for HelatGoal-View
        healthGoalName = (TextView) findViewById(R.id.txtHealthGoalName);
        healtGoalDataDescription = (TextView) findViewById(R.id.txtHealtGoalDataDescription);
        String healthGoalWeight = "Gesundheitsziel Gewicht";
        String healthGoalSteps = "Gesundheitsziel Schrittzähler";
        String dataDesriptionSteps = "geplannte Schritte in 24h";
        String dataDescriptionWeight = "geplanntes Gewicht in Kg";
        if(goalType.equals("weightGoal")){
            healthGoalName.setText(healthGoalWeight);
            healtGoalDataDescription.setText(dataDescriptionWeight);
        }else if (goalType.equals("stepsGoal")){
            healthGoalName.setText(healthGoalSteps);
            healtGoalDataDescription.setText(dataDesriptionSteps);
        }


        findViews();
        setupQueue();

        if(previousPage.equals("aendern")){
            backendUrl = getBackendUrl(previousPage);
            getHealthGoalDataFromBackend(backendUrl);
        }

        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // String backendUrl = getBackendUrl(previousPage);
              //  setHealthGoalToBackend(backendUrl);


                if(previousPage.equals("aendern")){
                    previousPage = "put";
                    backendUrl = getBackendUrl(previousPage);
                    putHealthGoalDataToBackend(backendUrl);
                }
                else if(previousPage.equals("neu")){
                    backendUrl = getBackendUrl(previousPage);
                    setHealthGoalToBackend(backendUrl);
                }
            }
        });


        //getHealthGoalDataFromBackend(backendUrl);
        //setHealthGoalToBackend(backendUrl);

    }

    private String getBackendUrl(String previousPage){
        String backendUrl = "";
        //get PatientId
        patiendId = Preferences.loadActualPatientID(getApplicationContext());

        String goalTypeForUrl = "";
        if(goalType.equals("weightGoal")){
            goalTypeForUrl = "weight";
        }else if(goalType.equals("stepsGoal")){
            goalTypeForUrl = "steps";
        }

        //get correct Url
        if(previousPage.equals("aendern")){
            backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + "api/v1/goal/" + goalTypeForUrl + "/" + goalId;
        }else if(previousPage.equals("neu")){
            backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + "api/v1/goal/" + goalTypeForUrl + "/";
        }else if (previousPage.equals("put")){
            backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + "api/v1/goal/" + goalTypeForUrl + "/"  + goalId + "/";
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
        dateHealthGoal = findViewById(R.id.dateHealthGoal);
        healthGoalData = findViewById(R.id.txtHealthGoalData);
    }

}