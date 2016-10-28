package com.amrizal.example.cardviewtester;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MyRecyclerViewAdapter mAdapter;
    private ArrayList<DataObject> dataset;
    private TextView itemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemCount = (TextView) findViewById(R.id.item_count);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        //mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        initDataSet();
        mAdapter = new MyRecyclerViewAdapter(this, dataset);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG, "Position-" + viewHolder.getAdapterPosition() + ", Direction-" + direction);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(TAG, " Clicked on Item " + position);
            }
        });

        refreshItemCount();
    }

    private void refreshItemCount() {
        itemCount.setText("" + dataset.size() + " item(s)");
    }

    private void initDataSet() {
        dataset = new ArrayList<>();
        for (int index = 0; index < 2; index++) {
            DataObject obj = new DataObject("Some Primary Text " + index,
                    "Secondary " + index);
            dataset.add(index, obj);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(TAG, " Clicked on Item " + position);
            }
        });*/
    }

    public void onAddItem(View view) {
        if(dataset == null){
            return;
        }

        int count = dataset.size();
        dataset.add(new DataObject("Some Primary Text " + count, "Secondary " + count));
        mAdapter.notifyDataSetChanged();

        refreshItemCount();
    }

    public void onRemoveItem(View view) {
        if(dataset == null || dataset.size() == 0){
            return;
        }

        int count = dataset.size();
        dataset.remove(count-1);
        mAdapter.notifyDataSetChanged();

        refreshItemCount();
    }
}
