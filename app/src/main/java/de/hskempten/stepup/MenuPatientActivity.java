package de.hskempten.stepup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;

public class MenuPatientActivity extends AppCompatActivity {

    LinearLayout viewSettings;
    ImageButton btnSettings;
    FloatingActionButton btnNewObservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_patient);

        // setting variables
        btnSettings = findViewById(R.id.btnSettings);
        viewSettings = findViewById(R.id.viewSettings);

        // hiding settings
        viewSettings.setVisibility(View.GONE);

        // setting the OnClick Listener
        btnSettings.setOnClickListener(v -> toggleVisibility());

        // Opening Summary Activity
        Button btnSummary = findViewById(R.id.btnSummary);
        btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPatientActivity.this, SummaryActivity.class);
                MenuPatientActivity.this.startActivity(intent);
            }
        });

        // Opening Patient Data Activity
        Button btnPatientData = findViewById(R.id.btnPatientData);
        btnPatientData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPatientActivity.this, PatientDataActivity.class);
                startActivity(intent);
            }
        });

        // Opening FHIR Settings Activity when "Servereinstellungen" Button clicked
        Button btnFHIRServerSettings = findViewById(R.id.btnFHIRServerSettings);
        btnFHIRServerSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(MenuPatientActivity.this, SettingsActivity.class);
                MenuPatientActivity.this.startActivity(settingsIntent);
            }
        });

        btnNewObservation = findViewById(R.id.btnNewObservation);
        Log.d("foo", "onCreate: " + btnNewObservation);
        btnNewObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(MenuPatientActivity.this, ChooseObservationType.class);
                MenuPatientActivity.this.startActivity(settingsIntent);
            }
        });
    }

    protected void toggleVisibility() {
        if (viewSettings.getVisibility() == View.GONE) viewSettings.setVisibility(View.VISIBLE);
        else viewSettings.setVisibility(View.GONE);
    }
}
