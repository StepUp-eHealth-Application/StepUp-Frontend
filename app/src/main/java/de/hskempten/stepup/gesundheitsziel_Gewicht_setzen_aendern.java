package de.hskempten.stepup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class gesundheitsziel_Gewicht_setzen_aendern extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesundheitsziel__gewicht_setzen_aendern);

        Spinner einheitSpinner = (Spinner) findViewById(R.id.spinner1);

        ArrayAdapter <String> einheitAdapter = new ArrayAdapter<String>(gesundheitsziel_Gewicht_setzen_aendern.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.einheiten));

        einheitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        einheitSpinner.setAdapter(einheitAdapter);
    }
}