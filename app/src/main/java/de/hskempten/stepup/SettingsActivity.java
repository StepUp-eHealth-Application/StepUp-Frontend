package de.hskempten.stepup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.uhn.fhir.util.UrlUtil;
import de.hskempten.stepup.preferences.Preferences;

public class SettingsActivity extends AppCompatActivity {

    EditText txtFhirServerURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        txtFhirServerURL = findViewById(R.id.etxtFhirServer);

        // Displaying FHIR Server URL if already provided
        String fhirServerURL = Preferences.loadFhirServerUrl(SettingsActivity.this);
        if (fhirServerURL != null) {
            txtFhirServerURL.setText(fhirServerURL);
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

                // Checking if given URL is valid
                if (UrlUtil.isValid(url)) {
                    text = "Successfully saved URL";
                    Preferences.saveFhirServerUrl(url, SettingsActivity.this);
                } else {
                    text = "The URL is not valid";
                }

                Toast.makeText(context, text, duration).show();
            }
        });
    }
}