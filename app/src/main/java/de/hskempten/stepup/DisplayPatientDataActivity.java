package de.hskempten.stepup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import de.hskempten.stepup.helpers.APIEndpoints;
import de.hskempten.stepup.preferences.Preferences;

public class DisplayPatientDataActivity extends AppCompatActivity {

    private static final String TAG = "DisplayPatientData";

    TextView name;
    TextView gender;
    TextView address;
    TextView postalCode;
    TextView city;
    TextView country;

    RequestQueue requestQueue;

    int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_patient_data);

        findViews();

        String patientId = Preferences.loadSelectedPatientID(getApplicationContext());
        if (patientId == null || patientId == "") {
            patientId = Preferences.loadPatientID(getApplicationContext());
        }
        String backendUrl = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.PATIENT + patientId;
        Log.d(TAG, "Backend URL: " + backendUrl);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // Sending data to backend
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.GET, backendUrl, new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            name.setText(response.getString("firstName") + " " + response.getString("lastName"));
                            gender.setText(response.getString("gender"));
                            address.setText(response.getString("street"));
                            postalCode.setText(response.getString("postalCode"));
                            city.setText(response.getString("city"));
                            country.setText(response.getString("country"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d(TAG, "onResponse: success");
                        Toast.makeText(getApplicationContext(), "Patientendaten erfolgreich geladen!", duration).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onResponse: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Patientendaten konnten nicht geladen werden!", duration).show();
                    }
                }
        ){

        };
        requestQueue.add(jsonobj);


    }

    private void findViews() {
        name = findViewById(R.id.txtName);
        gender = findViewById(R.id.txtGender);
        address = findViewById(R.id.txtAddresse);
        postalCode = findViewById(R.id.txtPatientPostalCode);
        city = findViewById(R.id.txtPatientCity);
        country = findViewById(R.id.txtPatientCountry);
    }
}