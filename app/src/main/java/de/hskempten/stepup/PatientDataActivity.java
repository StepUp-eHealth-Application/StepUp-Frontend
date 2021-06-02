package de.hskempten.stepup;

import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.hl7.fhir.r4.model.*;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;

import ca.uhn.fhir.parser.DataFormatException;
import de.hskempten.stepup.preferences.Preferences;
import de.hskempten.stepup.helpers.APIEndpoints;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;


public class PatientDataActivity extends AppCompatActivity {

    private static String LOG_TAG = "Patient_Data";

    EditText txtFirstName;
    EditText txtLastName;
    Spinner genderSpinner;
    EditText txtCountry;
    EditText txtCity;
    EditText txtPostalCode;
    EditText txtStreet;

    String firstName;
    String lastName;
    String gender;
    String country;
    String postalCode;
    String street;
    String city;

    TextView txtResponse;

    //RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_data);

        //requestQueue = new Volley.newRequestQueue(getApplicationContext());


        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtCountry = findViewById(R.id.txtCountry);
        txtCity = findViewById(R.id.txtCity);
        txtPostalCode = findViewById(R.id.txtPostalCode);
        txtStreet = findViewById(R.id.txtStreet);
        txtResponse =findViewById(R.id.txtResponse);

        txtResponse.setText("");


        // filling gender spinner
         genderSpinner = (Spinner) findViewById(R.id.spinnerGender);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //Alert Dialog Button
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientDataActivity.this);
        builder.setCancelable(true)
                .setTitle("Bestätigung")
                .setMessage("Möchten Sie die Daten wirklich abspeichern?");
        builder.setPositiveButton("Bestätigen",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //for Toast Message (Request Feedback Popup)
                        CharSequence confirmText;
                        Context context = getApplicationContext();
                        int duration = Toast.LENGTH_SHORT;


                        String fhirServer = Preferences.loadFhirServerUrl(PatientDataActivity.this);
                        String backendServer = Preferences.loadBackendUrl(PatientDataActivity.this);



                        //prepare HashMap data for POST-Request
                        HashMap<String, String> data = new HashMap<>();
                        data.put("firstName", firstName);
                        data.put("lastName", lastName);
                        data.put("gender", gender);
                        data.put("street", street);
                        data.put("postalCode", postalCode);
                        data.put("city", city);
                        data.put("country", country);
                        data.put("fhirServer", fhirServer);

                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                        Log.i(LOG_TAG, "Backend URL: " + backendServer);
                        //sending data to backend
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

                                        Log.i(LOG_TAG, "onResponse: success");
                                        //Save Patient ID
                                        Preferences.savePatientID(id, PatientDataActivity.this);
                                        //Feedback Popup
                                        CharSequence feedbackText = "Patient mit der ID: "+ Preferences.loadPatientID(PatientDataActivity.this) + " erfolgreich hinzugefügt!";
                                        Toast.makeText(context, feedbackText, duration).show();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.i(LOG_TAG, "onResponse: " + error.getMessage());
                                        //Feedback Popup
                                        CharSequence feedbackText = "Fehler! Patient konnte nicht hinzugefügt werden.";
                                        Toast.makeText(context, feedbackText, duration).show();
                                    }
                                }
                        ){

                        };
                        requestQueue.add(jsonobj);

                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //open confirmation Button, if "Speichern" Button is clicked
        Button submitButton = (Button)findViewById(R.id.btnSave);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                firstName = txtFirstName.getText().toString();
                lastName = txtLastName.getText().toString();
                gender = genderSpinner.getSelectedItem().toString();
                country = txtCountry.getText().toString();
                postalCode = txtPostalCode.getText().toString();
                street = txtStreet.getText().toString();
                city = txtCity.getText().toString();

                // check for input data
                boolean checkData = true;

                if (TextUtils.isEmpty(firstName)) {
                    txtFirstName.setError("Bitte Namen eingeben");
                    checkData = false;
                }if (TextUtils.isEmpty(lastName)){
                    txtLastName.setError("Bitte Nachnamen eingeben");
                    checkData = false;
                }if (TextUtils.isEmpty(country)){
                    txtCountry.setError("Bitte Land eingeben");
                    checkData = false;
                }if (TextUtils.isEmpty(postalCode)){
                    txtPostalCode.setError("Bitte PLZ eingeben");
                    checkData = false;
                }if (TextUtils.isEmpty(street)){
                    txtStreet.setError("Bitte Straße eingeben");
                    checkData = false;
                }if (TextUtils.isEmpty(city)){
                    txtCity.setError("Bitte Stadt eingeben");
                    checkData = false;
                }


                if(checkData == true){
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }



            }
        });



    }



}