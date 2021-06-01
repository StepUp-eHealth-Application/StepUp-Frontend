package de.hskempten.stepup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import org.hl7.fhir.r4.model.*;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
//import org.hl7.fhir.dstu3.model.Patient;
import java.io.IOException;
import ca.uhn.fhir.parser.DataFormatException;


public class PatientDataActivity extends AppCompatActivity {
    String txtFirstName;
    String txtLastName;
    String txtGender;
    String txtCountry;
    String txtCity;
    String txtPostalCode;
    String txtStreet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_data);

        // filling gender spinner
        Spinner genderSpinner = (Spinner) findViewById(R.id.spinnerGender);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);




        //Alert Dialog Button
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientDataActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Bestätigung");
        builder.setMessage("Möchten Sie die Daten wirklich abspeichern?");
        builder.setPositiveButton("Bestätigen",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if "Bestätigen" clicked
                        getInputData();
                        FhirContext ctx = FhirContext.forR4();
                        String serverBaseUrl = "http:http://hapi.fhir.org/baseR4";
                        Patient patient = new Patient();
                        patient.addIdentifier()
                                .setSystem("http:http://hapi.fhir.org/baseR4")
                                .setValue("122321457239486832374237823234233431");
                        patient.addName()
                                .setFamily(txtLastName)
                                .addGiven(txtFirstName);
                        //patient.getGenderElement().setValueAsString("Male");

                        ctx.getRestfulClientFactory().setConnectionRequestTimeout(60 * 1000);
                        ctx.getRestfulClientFactory().setSocketTimeout(60 * 1000);

                        IGenericClient client = ctx.newRestfulGenericClient(serverBaseUrl);

                        try{
                            MethodOutcome outcome = client.create()
                                    .resource(patient)
                                    .prettyPrint()
                                    .encodedXml()
                                    .execute();

                            IdType id = (IdType) outcome.getId();
                            IParser xmlParser = ctx.newXmlParser().setPrettyPrint(true);
                        }catch(DataFormatException e){
                            System.out.println("An error occurred trying to upload:");
                            e.printStackTrace();
                        }

                        /*
                        // create a new XML parser and serialize our Patient object with it
                        String encoded = ctx.newXmlParser().setPrettyPrint(true)
                                .encodeResourceToString(patient);

                        // Give the patient a temporary UUID so that other resources in
                        // the transaction can refer to it
                        patient.setId(IdType.newRandomUuid());
*/

                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //open confirmation Button, if "Speichern" Button is clicked
        Button submitButton = (Button)findViewById(R.id.btnSave);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //if Button confirmation Dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });






    }

    void getInputData(){
        //getting User Input

        //getting First Name
        EditText FirstName = (EditText) findViewById(R.id.txtFirstName);
        txtFirstName = FirstName.getText().toString();

        //getting  Last Name
        EditText LastName = (EditText) findViewById(R.id.txtLastName);
        txtLastName = LastName.getText().toString();

        //getting Gender
        Spinner genderSpinner = (Spinner) findViewById(R.id.spinnerGender);
        txtGender = genderSpinner.getSelectedItem().toString();

        //getting Address
        //Street
        EditText Street = (EditText) findViewById(R.id.txtStreet);
        txtStreet = Street.getText().toString();
        //Postal Code
        EditText PostalCode = (EditText) findViewById(R.id.txtPostalCode);
        txtPostalCode = PostalCode.getText().toString();
        //City
        EditText City = (EditText) findViewById(R.id.txtCity);
        txtCity = City.getText().toString();
        //Country
        EditText Country = (EditText) findViewById(R.id.txtCountry);
        txtCountry = Country.getText().toString();
    }

    void uploadPatientData(){

    }




}