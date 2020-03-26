package com.example.testingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class customListAdapter extends ArrayAdapter<Task> {


    private LayoutInflater inflater;
    private ArrayList<Task> arrayList;
    private int viewResourceId;

    public customListAdapter(Context context, int viewResourceId, ArrayList<Task> arrayList ) {

        super(context, viewResourceId, arrayList);
        this.arrayList = arrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(viewResourceId, null);

        Task task = arrayList.get(position);

        if (task != null) {

            TextView title = (TextView) convertView.findViewById(R.id.textTaskName);
            TextView category = (TextView) convertView.findViewById(R.id.textCategory);
            TextView data = (TextView) convertView.findViewById(R.id.textDate);



            if (title != null) {
                title.setText(task.getName());
            }
            if (category != null) {
                category.setText((task.getCategory()));
            }

            if (data != null) {
                data.setText((task.getDate()));
            }

        }


        return convertView;
    }

}
