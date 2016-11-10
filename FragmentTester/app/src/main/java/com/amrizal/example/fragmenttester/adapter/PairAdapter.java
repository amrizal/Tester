package com.amrizal.example.fragmenttester.adapter;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.amrizal.example.fragmenttester.R;

import java.util.List;

/**
 * Created by amrizal.zainuddin on 10/11/2016.
 */
public class PairAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    List<Pair<String, Boolean>> data;
    int activeIndex = -1;
    private PairAdapterCallback listener;

    public PairAdapter(Context context, PairAdapterCallback callback, List<Pair<String, Boolean>> data) {
        this.data = data;
        this.listener = callback;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Pair<String, Boolean> getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.item_layout, null);
            holder = new ViewHolder();
            holder.left = (TextView) view.findViewById(R.id.left_value);
            holder.right = (TextView) view.findViewById(R.id.right_value);
            holder.actionLayout = view.findViewById(R.id.action_layout);
            holder.button1 = (Button) view.findViewById(R.id.button_1);
            holder.button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemButton1Clicked(activeIndex);
                }
            });
            holder.toggle = (Switch) view.findViewById(R.id.toggle_1);
            view.setTag(holder);
        }else {
            holder = (ViewHolder)view.getTag();
        }

        holder.toggle.setOnCheckedChangeListener(null);
        Pair<String, Boolean> item = data.get(i);
        if(item != null){
            holder.left.setText(item.first);
            holder.toggle.setChecked(item.second);
        }

        holder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listener.onItemCheckChanged(activeIndex, b);
            }
        });

        holder.actionLayout.setVisibility(i == activeIndex? View.VISIBLE:View.GONE);

        return view;
    }

    private class ViewHolder {
        public TextView left;
        public TextView right;
        public View actionLayout;
        public Button button1;
        public Switch toggle;
    }

    public interface PairAdapterCallback {
        void onItemButton1Clicked(int position);
        void onItemCheckChanged(int position, boolean checked);
    }
}
