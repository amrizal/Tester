package com.amrizal.example.listviewtester;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by amrizal.zainuddin on 15/3/2016.
 */
public class ItemAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

    private final Context context;
    private ArrayList<Document> data;
    private LayoutInflater inflater;

    private CompoundButton.OnCheckedChangeListener toggleListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = (int) buttonView.getTag();
            Document document = (Document) getItem(position);
            if(null == document)
                return;

            document.setEnable(isChecked);
        }
    };

    public ItemAdapter(Context context) {
        this.context = context;
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
        return data.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("getView " + position + " " + convertView);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.document_item, null);
            holder = new ViewHolder();
            holder.label = (TextView)convertView.findViewById(R.id.document_label);
            holder.description = (TextView)convertView.findViewById(R.id.document_description);
            holder.layout = convertView.findViewById(R.id.document_layout);
            holder.toggle = (Switch) convertView.findViewById(R.id.document_enable);
            holder.toggle.setOnCheckedChangeListener(this);
            //holder.toggle.setOnCheckedChangeListener(new ToggleCheckedChangedListener());
            //holder.toggle.setOnCheckedChangeListener(toggleListener);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        Document document = data.get(position);
        holder.label.setText(document.getTitle());
        holder.description.setText(document.getDescription());
        holder.toggle.setChecked(document.isEnable());
        holder.toggle.setTag(position);

        if(document.getStatus().equalsIgnoreCase(Document.ACTIVE))
            holder.layout.setBackgroundColor(Color.YELLOW);
        else
            holder.layout.setBackgroundColor(Color.TRANSPARENT);

        return convertView;
    }

    public void setData(ArrayList<Document> data) {
        this.data = data;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.document_enable) {
            View parentRow = (View) buttonView.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);
            Document document = (Document) getItem(position);
            if (null != document) {
                document.setEnable(isChecked);
            }
        }
    }

    public static class ViewHolder {
        public TextView label;
        public TextView description;
        public View layout;
        public Switch toggle;
    }

    private class ToggleCheckedChangedListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = (int) buttonView.getTag();
            Document document = (Document) getItem(position);
            if(null == document)
                return;

            document.setEnable(isChecked);
        }
    }
}
