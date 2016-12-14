package com.amrizal.example.activelistitemtester;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

/**
 * Created by amrizal.zainuddin on 7/11/2016.
 */
public class ItemAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private OnDataItemClickListener listener;
    private List<DataItem> data;
    private long active = -1;

    public ItemAdapter(Context context, List<DataItem> data) {
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListener(OnDataItemClickListener listener) {
        this.listener = listener;
    }

    public void setActive(long active) {
        this.active = active;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return data.get(i).getId();
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.data_item, null);
            holder = new ViewHolder();
            holder.label = (TextView)convertView.findViewById(R.id.item_id);
            holder.panel = convertView.findViewById(R.id.item_panel);
            holder.button1 = (Button) convertView.findViewById(R.id.button_1);
            holder.button2 = (Button) convertView.findViewById(R.id.button_2);
            holder.listView = (ListView) convertView.findViewById(R.id.item_list);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        DataItem item = data.get(i);
        holder.label.setText("Item " + String.valueOf(item.getId()));
        holder.panel.setVisibility(i == active? View.VISIBLE:View.GONE);
        holder.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onButton1Clicked(i);
            }
        });

        holder.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onButton2Clicked(i);
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        public TextView label;
        public View panel;
        public Button button1;
        public Button button2;
        public ListView listView;
    }

    public interface OnDataItemClickListener{
        public void onButton1Clicked(int position);
        public void onButton2Clicked(int position);
    }
}
