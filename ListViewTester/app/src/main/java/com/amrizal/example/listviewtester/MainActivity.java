package com.amrizal.example.listviewtester;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Document> data = new ArrayList<>();
    int currentActiveIndex = -1;
    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeData();

        itemAdapter = new ItemAdapter(this);
        itemAdapter.setData(data);

        ListView listView = (ListView) findViewById(R.id.document_list);
        listView.setAdapter(itemAdapter);
    }

    private void initializeData() {
        for(int i=0; i<10; i++){
            Document document = new Document();
            int id = i+1;
            document.setId(id);
            document.setTitle("Title " + String.valueOf(id));
            document.setDescription("Description " + String.valueOf(id));
            data.add(document);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_down:
                setNextItemActive();
                break;
            case R.id.menu_item_up:
                setPreviousItemActive();
                break;
            default:
                break;
        }
        return true;
    }

    void setNextItemActive(){
        currentActiveIndex++;
        if(currentActiveIndex == data.size()){
            currentActiveIndex = 0;
        }

        updateData();
    }

    void setPreviousItemActive(){
        currentActiveIndex--;
        if(currentActiveIndex < 0){
            currentActiveIndex = data.size() - 1;
        }

        updateData();
    }

    private void updateData() {
        for(int i=0; i<data.size(); i++){
            Document document = data.get(i);
            if(i == currentActiveIndex)
                document.setStatus(Document.ACTIVE);
            else
                document.setStatus("");
        }

        itemAdapter.notifyDataSetChanged();
    }
}
