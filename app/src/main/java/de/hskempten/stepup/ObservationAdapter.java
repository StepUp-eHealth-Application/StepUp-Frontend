package de.hskempten.stepup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ObservationAdapter extends ArrayAdapter<DataModelObservation> {

    private static final String TAG = "ObservationAdapter";

    private ArrayList<DataModelObservation> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView device;
        TextView date;
        TextView accomplished;
    }

    public ObservationAdapter(ArrayList<DataModelObservation> data, Context context) {
        super(context, R.layout.listitem_observation, data);
        this.dataSet = data;
        this.mContext = context;
        Log.d(TAG, "created GoalAdapter");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "executed: getView()");
        DataModelObservation dataModelObservation = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listitem_observation, parent, false);
            viewHolder.device = (TextView) convertView.findViewById(R.id.txtDevice);
            viewHolder.date = (TextView) convertView.findViewById(R.id.txtDate);
            viewHolder.accomplished = (TextView) convertView.findViewById(R.id.txtAccomplished);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.device.setText(dataModelObservation.getDevice());
        viewHolder.date.setText(dataModelObservation.getDate());
        viewHolder.accomplished.setText(dataModelObservation.getAccomplished());
        Log.d(TAG, "returning viewHolder");
        return convertView;
    }
}
