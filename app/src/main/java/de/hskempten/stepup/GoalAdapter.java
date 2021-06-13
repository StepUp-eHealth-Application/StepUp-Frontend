package de.hskempten.stepup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class GoalAdapter extends ArrayAdapter<DataModelGoal> {

    private static final String TAG = "GoalAdapter";

    private ArrayList<DataModelGoal> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView description;
        TextView dueDate;
        TextView goal;
        TextView accomplished;
    }

    public GoalAdapter(ArrayList<DataModelGoal> data, Context context) {
        super(context, R.layout.listitem_goal, data);
        this.dataSet = data;
        this.mContext = context;
        Log.d(TAG, "created GoalAdapter");
    }

    /*@Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        DataModelGoal dataModelGoal = (DataModelGoal) object;

        String goalId = dataModelGoal.getId();

        Intent intent = new Intent(mContext.getApplicationContext(), beobachtung_anzeigen.class);
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "executed: getView()");
        DataModelGoal dataModelGoal = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listitem_goal, parent, false);
            viewHolder.description = (TextView) convertView.findViewById(R.id.txtDescription);
            viewHolder.dueDate = (TextView) convertView.findViewById(R.id.txtDueDate);
            viewHolder.goal = (TextView) convertView.findViewById(R.id.txtGoal);
            viewHolder.accomplished = (TextView) convertView.findViewById(R.id.txtAccomplished);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.description.setText(dataModelGoal.getDescription());
        viewHolder.dueDate.setText(dataModelGoal.getDueDate());
        viewHolder.goal.setText(dataModelGoal.getGoal());
        viewHolder.accomplished.setText(dataModelGoal.getAccomplished());
        Log.d(TAG, "returning viewHolder");
        return convertView;
    }
}
