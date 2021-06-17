package de.hskempten.stepup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import de.hskempten.stepup.preferences.Preferences;


public class MainActivity extends AppCompatActivity {

    Button btnPatient;
    Button btnLe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPatient = (Button) findViewById(R.id.btnPatient);
        btnLe = (Button) findViewById(R.id.btnLe);
        btnPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                Preferences.saveSelectedPatientID(null, getApplicationContext());
                openNewActivity(intent);
            }
        });
        btnLe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchPatientActivity.class);
                openNewActivity(intent);
            }
        });
    }

    private void openNewActivity(Intent intent) {
        startActivity(intent);
    }
}
