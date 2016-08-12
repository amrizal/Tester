package com.amrizal.example.bottomtabtester;

import android.content.ClipData;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends BaseActivity {

    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_tabs);
        // mTabHost = new FragmentTabHost(this);
        // mTabHost.setup(this, getSupportFragmentManager(),
        // R.id.menu_settings);
        setupActionBar(getString(R.string.app_name));

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        Bundle b = new Bundle();
        b.putString("key", "Simple");
        mTabHost.addTab(mTabHost.newTabSpec("simple").setIndicator("Simple"),
                ItemFragment.class, b);
        //
        b = new Bundle();
        System.out.print("hello git");
        b.putString("key", "Contacts");
        mTabHost.addTab(mTabHost.newTabSpec("contacts")
                .setIndicator("Contacts"), ItemFragment.class, b);
        b = new Bundle();
        b.putString("key", "Custom");
        mTabHost.addTab(mTabHost.newTabSpec("custom").setIndicator("Custom"),
                ItemFragment.class, b);
        // setContentView(mTabHost);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
