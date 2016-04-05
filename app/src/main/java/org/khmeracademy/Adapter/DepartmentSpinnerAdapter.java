package org.khmeracademy.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.khmeracademy.Model.DepartmentItem;

import java.util.ArrayList;

/**
 * Created by Longdy on 2/11/2016.
 */
public class DepartmentSpinnerAdapter extends ArrayAdapter<DepartmentItem> {

    int groupid;
    Activity context;
    ArrayList<DepartmentItem> list;
    LayoutInflater inflater;

    public DepartmentSpinnerAdapter(Activity context, int groupid, int id, ArrayList<DepartmentItem> list) {
        super(context, id, list);
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid = groupid;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = inflater.inflate(groupid, parent, false);
        TextView uName = (TextView) itemView.findViewById(org.khmeracademy.R.id.university_name);
        uName.setText(list.get(position).getName());
        return itemView;
    }

}
