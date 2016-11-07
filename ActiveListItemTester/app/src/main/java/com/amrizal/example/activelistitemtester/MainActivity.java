package com.amrizal.example.activelistitemtester;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemAdapter.OnDataItemClickListener {

    List<DataItem> data;
    private ItemAdapter itemAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.my_list);

        data = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, data);
        itemAdapter.setListener(this);
        listView.setAdapter(itemAdapter);

        for(int i=0; i<100; i++){
            long id = i + 1;
            DataItem dataItem = new DataItem(id);
            dataItem.add(id + "_1");
            dataItem.add(id + "_2");
            dataItem.add(id + "_3");

            data.add(dataItem);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemAdapter.setActive(i);
                itemAdapter.notifyDataSetChanged();
                if(i == (data.size()-1)) {
                    listView.setSelection(i);
                }
            }
        });
    }

    @Override
    public void onButton1Clicked(int position) {
        DataItem dataItem = data.get(position);
        if(dataItem == null){
            return;
        }

        Toast.makeText(this, dataItem.get(0), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onButton2Clicked(int position) {
        DataItem dataItem = data.get(position);
        if(dataItem == null){
            return;
        }

        Toast.makeText(this, dataItem.get(1), Toast.LENGTH_SHORT).show();
    }
}
