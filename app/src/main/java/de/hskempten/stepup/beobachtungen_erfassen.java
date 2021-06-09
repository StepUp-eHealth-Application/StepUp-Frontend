package de.hskempten.stepup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class beobachtungen_erfassen extends AppCompatActivity {

    EditText eTxtNumber;
    DatePicker dpObservationSteps;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beobachtungen_erfassen);

        findViews();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void findViews() {
        eTxtNumber = findViewById(R.id.eTxtNumberSteps);
        dpObservationSteps = findViewById(R.id.dpObservationSteps);
        save = findViewById(R.id.btnSaveObservationSteps);
    }
}