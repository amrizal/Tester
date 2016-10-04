package com.stationalertapp.malaysia.activitytester;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    public static final String VALUE = "valueText";
    private TextView valueText;
    private boolean visibleView1 = true;
    private boolean visibleView2 = true;
    private boolean visibleView3 = true;
    private boolean visibleView4 = true;
    private boolean visibleView5 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        valueText = (TextView)findViewById(R.id.value);

        Button dismiss = (Button)findViewById(R.id.button_dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if(null == intent)
            return;

        String value = intent.getStringExtra(VALUE);
        valueText.setText(value);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.toggle_view1:
                visibleView1 = !visibleView1;
                refreshView(R.id.view_1, visibleView1);
                break;
            case R.id.toggle_view2:
                visibleView2 = !visibleView2;
                refreshView(R.id.view_2, visibleView2);
                break;
            case R.id.toggle_view3:
                visibleView3 = !visibleView3;
                refreshView(R.id.view_3, visibleView3);
                break;
            case R.id.toggle_view4:
                visibleView4 = !visibleView4;
                refreshView(R.id.view_4, visibleView4);
                break;
            case R.id.toggle_view5:
                visibleView5 = !visibleView5;
                refreshView(R.id.view_5, visibleView5);
                break;
            default:
                break;
        }
    }

    private void refreshView(int id, boolean isVisible) {
        View view = findViewById(id);
        if(isVisible){
            view.setVisibility(View.VISIBLE);
        }else{
            view.setVisibility(View.GONE);
        }
    }
}
