package de.hskempten.stepup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseObservationType extends AppCompatActivity {

    Button btnSteps;
    Button btnWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_observation_type);

        btnSteps = findViewById(R.id.btnStartStepsObservationActivity);
        btnSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(ChooseObservationType.this, beobachtungen_erfassen.class);
                ChooseObservationType.this.startActivity(settingsIntent);
            }
        });

        btnWeight = findViewById(R.id.btnStartWeightObservationActivity);
        btnWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(ChooseObservationType.this, beobachtung_erfassen_gewicht.class);
                ChooseObservationType.this.startActivity(settingsIntent);
            }
        });
    }
}