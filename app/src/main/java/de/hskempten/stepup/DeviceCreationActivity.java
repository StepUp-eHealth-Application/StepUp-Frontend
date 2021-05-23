package de.hskempten.stepup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.Reference;

import java.security.acl.AclNotFoundException;
import java.util.HashMap;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.hskempten.stepup.fhir.FhirClient;
import de.hskempten.stepup.preferences.Preferences;

public class DeviceCreationActivity extends AppCompatActivity {

    private static String LOG_TAG = "DEVICE_CREATION";

    EditText txtDeviceName;
    EditText txtManufacturer;
    Spinner statusSpinner;
    Spinner deviceTypeSpinner;
    EditText txtNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_creation);

        txtDeviceName = findViewById(R.id.txtDeviceName);
        txtManufacturer = findViewById(R.id.txtManufacturer);
        txtNote = findViewById(R.id.txtNote);

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
                String fhirServer = Preferences.loadFhirServerUrl();
                String patientID = Preferences.loadSelectedPatientID(DeviceCreationActivity.this);
                if (patientID == null) {
                    patientID = Preferences.loadPatientID(DeviceCreationActivity.this);
                }

                String deviceName = txtDeviceName.getText().toString();
                String manufacturer = txtManufacturer.getText().toString();
                String deviceStatus = statusSpinner.getSelectedItem().toString();
                String deviceType = deviceTypeSpinner.getSelectedItem().toString();
                String note = txtNote.getText().toString();

                Device device = new Device();

                // Setting status
                HashMap<String, Device.FHIRDeviceStatus> status = new HashMap<>();
                status.put("Aktiv", Device.FHIRDeviceStatus.ACTIVE);
                status.put("Inaktiv", Device.FHIRDeviceStatus.INACTIVE);
                status.put("Fehlerhaft", Device.FHIRDeviceStatus.ENTEREDINERROR);
                status.put("Unbekannt", Device.FHIRDeviceStatus.UNKNOWN);

                device.setStatus(status.get(deviceStatus));

                // Setting the manufacturer
                device.setManufacturer(manufacturer);

                // Setting the device name
                device.addDeviceName()
                        .setName(deviceName)
                        .setType(Device.DeviceNameType.PATIENTREPORTEDNAME);

                // Setting device type
                CodeableConcept deviceTypeConcept = new CodeableConcept();

                HashMap<String, String> deviceTypeCodingValues = new HashMap<>();
                deviceTypeCodingValues.put("Waage", "5042005");
                deviceTypeCodingValues.put("Schrittzähler", "XXXXXX"); // TODO: find code

                Coding deviceTypeCoding = new Coding();
                deviceTypeCoding.setCode(deviceTypeCodingValues.get(deviceType));
                deviceTypeCoding.setDisplay(deviceType);
                deviceTypeCoding.setSystem("http://snomed.info/sct");

                deviceTypeConcept.addCoding(deviceTypeCoding);
                deviceTypeConcept.setText("Art des Gesundheitgeräts");

                device.setType(deviceTypeConcept);

                // Creating note for device
                Annotation deviceNote = new Annotation();
                deviceNote.setText(note);

                // Creating reference to patient
                Reference patientReference;
                if (patientID != null) {
                    patientReference = new Reference(patientID);
                    patientReference.setType("Patient");

                    deviceNote.setAuthor(patientReference);

                    device.setPatient(patientReference);
                }

                // Setting note for device
                device.addNote(deviceNote);

                Log.d(LOG_TAG, "Creating FHIR client");

                FhirContext ctx = FhirContext.forR4();
                IGenericClient client = ctx.newRestfulGenericClient("http://hapi.fhir.org/baseR4/");

                Device createDevice = (Device) client.create().resource(device).execute().getResource();
                //Log.i(LOG_TAG, "onClick: test");
            }
        });
    }
}