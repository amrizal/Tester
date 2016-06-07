package com.checkinapp.checkin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.checkinapp.checkin.model.NearbyPlace;
import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amrizal.zainuddin on 7/6/2016.
 */
public class PlaceAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;
    private ArrayList<NearbyPlace> data;

    public PlaceAdapter(Context context, ArrayList<NearbyPlace> data) {
        this.context = context;
        this.data = data;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.place_item, null);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.place_name);
            holder.description = (TextView)convertView.findViewById(R.id.place_description);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        NearbyPlace place = (NearbyPlace) getItem(position);
        if(place != null){
            holder.name.setText(place.getName());
            holder.description.setText(place.getDescription());
        }
        
        return convertView;
    }

    public static class ViewHolder {
        public TextView name;
        public TextView description;
    }
}
