package com.amrizal.example.wordpressexplorer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    String targetUrl = "http://www.thejavaprogrammer.com";///wp-json/wp/v2/posts?filter[posts_per_page]=10&fields=id,title";

    private TextView targetSite;
    private Button showSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_main);

        targetSite = (TextView)findViewById(R.id.target_site);
        targetSite.setText(targetUrl);
        showSite = (Button)findViewById(R.id.show_site);
        showSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SiteActivity.class);
                String url = String.valueOf(targetSite.getText());
                intent.putExtra(EXTRA.TARGET_URL, url);
                startActivityForResult(intent, REQUEST.SITE_ACTIVITY);
            }
        });
    }

    private class REQUEST {
        public static final int SITE_ACTIVITY = 1000;
    }
}
