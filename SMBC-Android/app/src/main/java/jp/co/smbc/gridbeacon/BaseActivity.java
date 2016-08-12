package jp.co.smbc.gridbeacon;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by amrizal.zainuddin on 12/8/2016.
 */
public class BaseActivity extends AppCompatActivity {

    private TextView actionBarTitle;
    private FrameLayout progressBar;

    protected void setupActionBar(String inTitle) {
        if (getSupportActionBar() != null) {

            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View mCustomView = layoutInflater.inflate(R.layout.custom_action_bar, null);

            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(mCustomView);

            progressBar = (FrameLayout) mCustomView.findViewById(R.id.progress);

            Toolbar parent = (Toolbar) mCustomView.getParent();//first get parent toolbar of current action bar
            parent.setContentInsetsAbsolute(0, 0);// set padding programmatically to 0dp

            actionBarTitle = (TextView) findViewById(R.id.action_bar_title);
            actionBarTitle.setText(Html.fromHtml(inTitle));
        }
    }
}
