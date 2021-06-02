package de.hskempten.stepup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//DEVELOPMETN
import android.content.Intent;

import ca.uhn.fhir.util.UrlUtil;
import de.hskempten.stepup.preferences.Preferences;

public class SettingsActivity extends AppCompatActivity {

    EditText txtFhirServerURL;
    EditText txtBackendUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        txtFhirServerURL = findViewById(R.id.etxtFhirServer);
        txtBackendUrl = findViewById(R.id.eTxtBackendServer);

        // Displaying FHIR Server URL if already provided
        String fhirServerURL = Preferences.loadFhirServerUrl(SettingsActivity.this);
        if (fhirServerURL != null) {
            txtFhirServerURL.setText(fhirServerURL);
        }

        // Displaying Backend URL if already provided
        String backendUrl = Preferences.loadBackendUrl(SettingsActivity.this);
        if (backendUrl != null) {
            txtBackendUrl.setText(backendUrl);
        }

        // Saving FHIR Server URL into preference
        Button btnSave = findViewById(R.id.btnSaveSettings);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence text;
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                String url = txtFhirServerURL.getText().toString();
                String backendUrl = txtBackendUrl.getText().toString();

                // Checking if given URL is valid
                if (UrlUtil.isValid(url) && UrlUtil.isValid(backendUrl)) {
                    text = "URLs wurden gespeichert";
                    Preferences.saveFhirServerUrl(url, SettingsActivity.this);
                    Preferences.saveBackendUrl(backendUrl, SettingsActivity.this);
                } else {
                    text = "Einer der URLs ist nicht gültig";
                }

                Toast.makeText(context, text, duration).show();
                //DEVELOPMENT
                openActivity();
            }
        });
    }
    public void openActivity(){
        Intent intent = new Intent(this, PatientDataActivity.class);
        startActivity(intent);
    }
}