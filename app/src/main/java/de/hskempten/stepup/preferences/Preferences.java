package de.hskempten.stepup.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static String PATIENT_ID = "PATIENT_ID";
    private static String FHIR_SERVER_URL = "FHIR_SERVER_URL";
    private static String SELECTED_PATIENT_ID = "SELECTED_PATIENT_ID";
    private static String BACKEND_URL = "BACKEND_URL";

    public static String loadFhirServerUrl(Activity activity) {
        return loadStringPreference(activity, FHIR_SERVER_URL);
    }

    public static void saveFhirServerUrl(String fhirServerUrl, Activity activity) {
        savePreference(fhirServerUrl, activity, FHIR_SERVER_URL);
    }

    public static String loadBackendUrl(Activity activity) {
        return loadStringPreference(activity, BACKEND_URL);
    }

    public static void saveBackendUrl(String backendUrl, Activity activity) {
        savePreference(backendUrl, activity, BACKEND_URL);
    }

    public static String loadPatientID(Activity activity) {
        return loadStringPreference(activity, PATIENT_ID);
    }

    public static void savePatientID(String patientID, Activity activity) {
        savePreference(patientID, activity, PATIENT_ID);
    }

    public static String loadSelectedPatientID(Activity activity) {
        return loadStringPreference(activity, SELECTED_PATIENT_ID);
    }

    public static void saveSelectedPatientID(String selectedPatientID, Activity activity) {
        savePreference(selectedPatientID, activity, SELECTED_PATIENT_ID);
    }

    private static String loadStringPreference(Activity activity, String key) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    private static void savePreference(String value, Activity activity, String key) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
