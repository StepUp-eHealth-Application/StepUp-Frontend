package de.hskempten.stepup;

import android.util.Log;

public class DataModelObservation {

    private static final String TAG = "DataModelObservation";

    String id;
    String device;
    String date;
    String accomplished;

    public DataModelObservation(String id, String device, String date, String accomplished) {
        this.id = id;
        this.device = device;
        this.date = date;
        this.accomplished = accomplished;
        Log.d(TAG, "new observation");
    }

    public String getId() {
        return id;
    }

    public String getDevice() {
        return device;
    }

    public String getDate() {
        return date;
    }

    public String getAccomplished() {
        return accomplished;
    }
}
