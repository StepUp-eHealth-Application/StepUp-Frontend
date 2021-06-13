package de.hskempten.stepup;

import android.util.Log;

public class DataModelGoal {

    private static final String TAG = "DataModelGoal";

    String id;
    String description;
    String dueDate;
    String goal;
    String accomplished;

    public DataModelGoal(String id, String description, String dueDate, String goal, String accomplished) {
        this.id = id;
        this.description = description;
        this.dueDate = dueDate;
        this.goal = goal;
        this.accomplished = accomplished;
        Log.d(TAG, "new Entry");
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getGoal() {
        return goal;
    }

    public String getAccomplished() {
        return accomplished;
    }
}
