package de.hskempten.stepup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

import de.hskempten.stepup.helpers.APIEndpoints;
import de.hskempten.stepup.preferences.Preferences;

public class SummaryActivity extends AppCompatActivity {

    private static final String TAG = "SummaryActivity";

    DatePicker summaryDate;
    Button btnCreateSummary;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        findViews();

        btnCreateSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = summaryDate.getYear() + "-";
                if (summaryDate.getMonth() + 1 < 10) {
                    date += "0";
                }
                date += summaryDate.getMonth() + "-" + summaryDate.getDayOfMonth();

                Log.d(TAG, "Date: " + date);

                String patientId = Preferences.loadActualPatientID(getApplicationContext());
                String backendServer = Preferences.loadBackendUrl(getApplicationContext()) + APIEndpoints.SUMMARY;

                HashMap<String, String> data = new HashMap<>();
                data.put("date", date);
                data.put("patientId", patientId);
                data.put("id", "");

                Log.i(TAG, "Backend URL: " + backendServer);
                // Sending data to backend
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

                                Log.i(TAG, "onResponse: success. Summary ID: " + id);
                                Toast.makeText(getApplicationContext(), "Zusammenfassung mit der ID " + id + " wurde erfolgreich erstellt!", Toast.LENGTH_LONG).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i(TAG, "onResponse: " + error.getMessage());
                            }
                        }
                ){

                };
                requestQueue.add(jsonobj);
            }
        });
    }

    private void findViews() {
        summaryDate = findViewById(R.id.summaryDatePicker);
        btnCreateSummary = findViewById(R.id.btnCreateSummary);
    }
}