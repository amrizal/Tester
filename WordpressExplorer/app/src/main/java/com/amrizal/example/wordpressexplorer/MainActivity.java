package com.amrizal.example.wordpressexplorer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView targetSite;
    private Button showSite;

    private String siteUrl = "https://ijustwanttotesttheapi.wordpress.com";//http://172.16.101.88/wp";//"http://www.thejavaprogrammer.com"
    private String siteUsername = "test_admin";
    private String sitePassword = "DVeaav8f42Orh7IgvC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_main);

        targetSite = (TextView)findViewById(R.id.target_site);
        targetSite.setText(siteUrl);

        showSite = (Button)findViewById(R.id.show_site);
        showSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SiteActivity.class);
                String url = String.valueOf(targetSite.getText());
                intent.putExtra(EXTRA.TARGET_URL, url);
                intent.putExtra(EXTRA.USERNAME, siteUsername);
                intent.putExtra(EXTRA.PASSWORD, sitePassword);
                startActivityForResult(intent, REQUEST.SITE_ACTIVITY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_CANCELED){
            if(data != null){
                String errorMessage = data.getStringExtra(EXTRA.ERROR_MESSAGE);
                if(errorMessage != null && !errorMessage.isEmpty()){
                    errorMessage = getString(R.string.error);
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class REQUEST {
        public static final int SITE_ACTIVITY = 1000;
    }
}
