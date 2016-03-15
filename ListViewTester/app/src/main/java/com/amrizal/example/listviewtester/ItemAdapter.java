package com.amrizal.example.listviewtester;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by amrizal.zainuddin on 15/3/2016.
 */
public class ItemAdapter extends BaseAdapter {

    private final Context context;
    private ArrayList<Document> data;
    private LayoutInflater inflater;

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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        Document document = data.get(position);
        holder.label.setText(document.getTitle());
        holder.description.setText(document.getDescription());

        if(document.getStatus().equalsIgnoreCase(Document.ACTIVE))
            holder.layout.setBackgroundColor(Color.YELLOW);
        else
            holder.layout.setBackgroundColor(Color.TRANSPARENT);

        return convertView;
    }

    public void setData(ArrayList<Document> data) {
        this.data = data;
    }

    public static class ViewHolder {
        public TextView label;
        public TextView description;
        public View layout;
    }
}
