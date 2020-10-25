package com.myapp.finalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class MyLvAdapter extends ArrayAdapter {
    private static final String TAG = "MyAdapter";
    public MyLvAdapter(@NonNull Context context, int resource,  @NonNull List objects) {
        super(context, resource, objects);
    }
    public View getView(int position, View convertView, ViewGroup parent){
        View itemView = convertView;
        if(itemView==null){
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.mylist2,parent,false);
        }
        Cityname map=(Cityname) getItem(position);
        TextView title = (TextView) itemView.findViewById(R.id.itemTitle);
        TextView detail = (TextView) itemView.findViewById(R.id.itemDetail);

        title.setText(map.getQuname());
        detail.setText(map.getPyname());

        return itemView;
    }
}
