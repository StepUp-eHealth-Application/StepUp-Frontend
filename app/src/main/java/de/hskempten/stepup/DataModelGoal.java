package de.hskempten.stepup;

import android.util.Log;

public class DataModelGoal {

    private static final String TAG = "DataModelGoal";

    String id;
    String description;
    String dueDate;
    String goal;
    String accomplished;
    String type;

    public DataModelGoal(String id, String description, String dueDate, String goal, String type) {
        this.id = id;
        this.description = description;
        this.dueDate = dueDate;
        this.goal = goal;
        this.type = type;
        Log.d(TAG, "new goal");
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

    public String getType() { return type; }
}
