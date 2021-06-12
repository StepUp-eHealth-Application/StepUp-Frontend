package de.hskempten.stepup.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    private static String PATIENT_ID = "PATIENT_ID";
    private static String FHIR_SERVER_URL = "FHIR_SERVER_URL";
    private static String SELECTED_PATIENT_ID = "SELECTED_PATIENT_ID";
    private static String BACKEND_URL = "BACKEND_URL";

    public static String loadActualPatientID(Context context) {
        String patientId = Preferences.loadSelectedPatientID(context);
        if (patientId == null || patientId == "") {
            patientId = Preferences.loadPatientID(context);
        }

        return patientId;
    }

    public static String loadFhirServerUrl(Context context) {
        return loadStringPreference(context, FHIR_SERVER_URL);
    }

    public static void saveFhirServerUrl(String fhirServerUrl, Context context) {
        savePreference(fhirServerUrl, context, FHIR_SERVER_URL);
    }

    public static String loadBackendUrl(Context context) {
        return loadStringPreference(context, BACKEND_URL);
    }

    public static void saveBackendUrl(String backendUrl, Context context) {
        savePreference(backendUrl, context, BACKEND_URL);
    }

    public static String loadPatientID(Context context) {
        return loadStringPreference(context, PATIENT_ID);
    }

    public static void savePatientID(String patientID, Context context) {
        savePreference(patientID, context, PATIENT_ID);
    }

    public static String loadSelectedPatientID(Context context) {
        return loadStringPreference(context, SELECTED_PATIENT_ID);
    }

    public static void saveSelectedPatientID(String selectedPatientID, Context context) {
        savePreference(selectedPatientID, context, SELECTED_PATIENT_ID);
    }

    private static String loadStringPreference(Context context, String key) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(key, null);
    }

    private static void savePreference(String value, Context context, String key) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
