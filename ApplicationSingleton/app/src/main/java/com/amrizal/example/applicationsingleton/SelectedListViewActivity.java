package com.amrizal.example.applicationsingleton;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectedListViewActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_list_view);

        String[] values = new String[] { "Item 1", "Item 2", "Item 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, values);
        setListAdapter(adapter);

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setItemChecked(2, true);
    }
}
