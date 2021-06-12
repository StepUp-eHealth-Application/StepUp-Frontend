package de.hskempten.stepup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hskempten.stepup.preferences.Preferences;

public class MenuLeActivity extends AppCompatActivity {

    TextView txtTitle;
    LinearLayout viewSettings;
    ImageButton btnSettings;
    String patientId;
    String patientName;

    FloatingActionButton btnNewObservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_le);

        // getting patient
        patientId = Preferences.loadSelectedPatientID(getApplicationContext());
        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText("Gesundheitsziele von\n" + patientId);

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
                Intent intent = new Intent(MenuLeActivity.this, SummaryActivity.class);
                startActivity(intent);
            }
        });

        // Opening Patient Data Activity
        Button btnPatientData = findViewById(R.id.btnPatientData);
        btnPatientData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuLeActivity.this, DisplayPatientDataActivity.class);
                startActivity(intent);
            }
        });

        btnNewObservation = findViewById(R.id.btnNewObservation2);
        Log.d("foo", "onCreate: " + btnNewObservation);
        btnNewObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getApplicationContext(), ChooseObservationType.class);
                MenuLeActivity.this.startActivity(settingsIntent);
            }
        });
    }

    protected void toggleVisibility() {
        if (viewSettings.getVisibility() == View.GONE) viewSettings.setVisibility(View.VISIBLE);
        else viewSettings.setVisibility(View.GONE);
    }
}
