package de.hskempten.stepup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.Reference;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.AclNotFoundException;
import java.util.HashMap;

import javax.net.ssl.HandshakeCompletedEvent;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepup.fhir.FhirClient;
import de.hskempten.stepup.helpers.APIEndpoints;
import de.hskempten.stepup.preferences.Preferences;

public class DeviceCreationActivity extends AppCompatActivity {

    private static String LOG_TAG = "DEVICE_CREATION";

    EditText txtDeviceName;
    EditText txtManufacturer;
    Spinner statusSpinner;
    Spinner deviceTypeSpinner;
    EditText txtNote;
    TextView txtResponse;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_creation);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        txtDeviceName = findViewById(R.id.txtDeviceName);
        txtManufacturer = findViewById(R.id.txtManufacturer);
        txtNote = findViewById(R.id.txtNote);
        txtResponse = findViewById(R.id.txtResponse);

        txtResponse.setText("");

        // Adding choices to the status spinner
        statusSpinner = (Spinner) findViewById(R.id.spinnerStatus);
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.device_status, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        // Adding choices to device type spinner
        deviceTypeSpinner = (Spinner) findViewById(R.id.spinnerDeviceType);
        ArrayAdapter<CharSequence> deviceTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.device_type, android.R.layout.simple_spinner_item);
        deviceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceTypeSpinner.setAdapter(deviceTypeAdapter);

        Button btnCreateDevice = findViewById(R.id.btnCreateDevice);
        btnCreateDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fhirServer = Preferences.loadFhirServerUrl(DeviceCreationActivity.this);
                String backendServer = Preferences.loadBackendUrl(DeviceCreationActivity.this) + APIEndpoints.DEVICE;

                String patientID = Preferences.loadSelectedPatientID(DeviceCreationActivity.this);
                if (patientID == null) {
                    patientID = Preferences.loadPatientID(DeviceCreationActivity.this);
                }

                String deviceName = txtDeviceName.getText().toString();
                String manufacturer = txtManufacturer.getText().toString();
                String deviceStatus = statusSpinner.getSelectedItem().toString();
                String deviceType = deviceTypeSpinner.getSelectedItem().toString();
                String note = txtNote.getText().toString();

                HashMap<String, String> data = new HashMap<>();
                data.put("name", deviceName);
                data.put("manufacturer", manufacturer);
                data.put("status", deviceStatus);
                data.put("type", deviceType);
                data.put("note", note);
                data.put("patientID", patientID);
                data.put("fhirServer", fhirServer);

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                Log.i(LOG_TAG, "Backend URL: " + backendServer);
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

                                Log.i(LOG_TAG, "onResponse: success");
                                String message = "Gerät erfolgreich abgespeichert. ID: " + id;
                                txtResponse.setText(message);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i(LOG_TAG, "onResponse: " + error.getMessage());
                                String message = "Fehler beim abspeichern";
                                txtResponse.setText(message);
                            }
                        }
                ){

                };
                requestQueue.add(jsonobj);
            }
        });
    }
}