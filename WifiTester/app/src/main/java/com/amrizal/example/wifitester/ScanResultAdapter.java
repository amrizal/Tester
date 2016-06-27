package com.amrizal.example.wifitester;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amrizal.zainuddin on 27/6/2016.
 */
public class ScanResultAdapter extends BaseAdapter {

    private final Context context;
    private List<ScanResult> data;
    private LayoutInflater inflater;

    public ScanResultAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(data == null)
            return -1;

        return data.size();
    }

    @Override
    public Object getItem(int position) {
        if(data == null)
            return null;

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
            convertView = inflater.inflate(R.layout.scan_result_item, null);
            holder = new ViewHolder();
            holder.ssid = (TextView)convertView.findViewById(R.id.ssid);
            holder.bssid = (TextView)convertView.findViewById(R.id.bssid);
            holder.capabilities = (TextView) convertView.findViewById(R.id.capabilities);
            holder.dbm = (TextView)convertView.findViewById(R.id.level);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        ScanResult scanResult = (ScanResult) getItem(position);
        if(scanResult != null) {
            holder.ssid.setText(scanResult.SSID.trim());
            holder.bssid.setText(String.format(context.getString(R.string.bssid), scanResult.BSSID.trim()));
            holder.capabilities.setText(String.format(context.getString(R.string.capabilities),scanResult.capabilities.trim()));
            holder.dbm.setText(String.format(context.getString(R.string.dbm), scanResult.level));
        }

        return convertView;
    }

    public void setData(List<ScanResult> data) {
        this.data = data;
    }

    public static class ViewHolder {
        public TextView ssid;
        public TextView bssid;
        public TextView capabilities;
        public TextView dbm;
    }
}
