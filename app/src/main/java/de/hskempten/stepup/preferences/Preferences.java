package de.hskempten.stepup.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static String PATIENT_ID = "PATIENT_ID";
    private static String FHIR_SERVER_URL = "FHIR_SERVER_URL";
    private static String SELECTED_PATIENT_ID = "SELECTED_PATIENT_ID";

    public static String loadFhirServerUrl() {
        // TODO: load from preference
        return "http://hapi.fhir.org/baseR4/";
    }

    public static void saveFhirServerUrl(String fhirServerUrl, Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(FHIR_SERVER_URL, fhirServerUrl);
        editor.apply();
    }

    public static String loadPatientID(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(PATIENT_ID, null);
    }

    public static void savePatientID(String patientID, Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PATIENT_ID, patientID);
        editor.apply();
    }

    public static String loadSelectedPatientID(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(SELECTED_PATIENT_ID, null);
    }

    public static void saveSelectedPatientID(String selectedPatientID, Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SELECTED_PATIENT_ID, selectedPatientID);
        editor.apply();
    }
}
