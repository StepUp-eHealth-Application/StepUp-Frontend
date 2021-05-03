package de.hskempten.stepup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class DeviceCreationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_creation);

        // Adding choices to the status spinner
        Spinner statusSpinner = (Spinner) findViewById(R.id.spinnerStatus);
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.device_status, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        // Adding choices to device type spinner
        Spinner deviceTypeSpinner = (Spinner) findViewById(R.id.spinnerDeviceType);
        ArrayAdapter<CharSequence> deviceTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.device_type, android.R.layout.simple_spinner_item);
        deviceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceTypeSpinner.setAdapter(deviceTypeAdapter);
    }
}