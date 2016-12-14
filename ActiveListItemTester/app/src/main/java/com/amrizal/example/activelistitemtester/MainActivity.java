package com.amrizal.example.activelistitemtester;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemAdapter.OnDataItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
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

        List<Pair<Integer, Integer>> col = new ArrayList<>();
        col.add(new Pair<>(1,2));
        col.add(new Pair<>(1,3));
        col.add(new Pair<>(1,4));
        col.add(new Pair<>(1,2));
        col.add(new Pair<>(2,2));
        col.add(new Pair<>(3,2));

        Pair<String, String> pair1 = new Pair<>("1","1");
        Pair<String, String> pair2 = new Pair<>("1","1");
        if(pair1 == pair2){
            Log.d(TAG, "Equals");
        }else{
            Log.d(TAG, "Not equals");
        }

        for(Pair<Integer, Integer> item:col){
            Log.d(TAG, "First:" + item.first + ", Second:" + item.second);
        }
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
