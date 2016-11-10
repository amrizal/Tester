package com.amrizal.example.fragmenttester;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FirstFragment.OnFragmentInteractionListener,
        SecondFragment.OnFragmentInteractionListener,
        ThirdFragment.OnFragmentInteractionListener,
        AlertDialogFragment.OnFragmentInteractionListener {

    private MenuItem fragmentGroup;
    private SubMenu fragmentMenu;
    private boolean start = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentGroup = navigationView.getMenu().getItem(0);
        fragmentMenu = fragmentGroup.getSubMenu();
    }

    private void initMainFragment() {
        final MainFragment mainFragment = MainFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_layout, mainFragment)
                .commitAllowingStateLoss();

        //setFragmentMenuChecked(0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch(item.getItemId()){
            case R.id.main_fragment:
                clearBackstacks();
                break;
            case R.id.first_fragment:
                showFirstFragment();
                break;
            case R.id.second_fragment:
                showSecondFragment();
                break;
            case R.id.third_fragment:
                showThirdFragment();
                break;
            case R.id.second_activity:
                showSecondActivity();
                break;
            case R.id.dialog_fagment:
                showDialogFragment();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearBackstacks() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        }
    }

    private void showDialogFragment() {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        //alertDialogFragment.setCancelable(false);//uncomment this to allow dismiss by clicking outside the dialogfragment
        alertDialogFragment.show(getSupportFragmentManager(), AlertDialogFragment.class.getSimpleName());
    }

    private void showSecondActivity() {
        Intent intent = new Intent(this, SecondActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);//uncomment this to dismiss the activity when app goes to background
        startActivityForResult(intent, REQUEST.SECOND_ACTIVITY);
    }

    private void showThirdFragment() {
        clearBackstacks();
        final ThirdFragment thirdFragment = ThirdFragment.newInstance("", "");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_layout, thirdFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();

        setFragmentMenuChecked(R.id.third_fragment);
    }

    private void showSecondFragment() {
        clearBackstacks();
        final SecondFragment secondFragment = SecondFragment.newInstance("", "");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_layout, secondFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();

        setFragmentMenuChecked(R.id.second_fragment);
    }

    private void showFirstFragment() {
        clearBackstacks();
        final FirstFragment firstFragment = FirstFragment.newInstance("", "");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_layout, firstFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();

        setFragmentMenuChecked(R.id.first_fragment);
    }

    void setFragmentMenuChecked(final int id){
        if(true){
            return;
        }

        for(int i=0; i<fragmentMenu.size(); i++){
            MenuItem item = fragmentMenu.getItem(i);
            item.setChecked(item.getItemId() == id);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST.SECOND_ACTIVITY:
                onResultSecondActivity(resultCode, data);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(start){
            start = false;
            initMainFragment();
        }
    }

    private void onResultSecondActivity(final int resultCode, final Intent data) {
        if(resultCode == Activity.RESULT_CANCELED || data == null){
            return;
        }

        switch (data.getIntExtra(EXTRA.FRAGMENT_TO_SHOW, -1)){
            case 1:
                showFirstFragment();
                break;
            case 2:
                showSecondFragment();
                break;
            case 3:
                showThirdFragment();
                break;
            default:
                break;
        }
    }

    @Override
    public void onAlertDialogFragmentShowFragment(int id) {
        switch (id){
            case R.id.first_fragment:
                showFirstFragment();
                break;
            case R.id.second_fragment:
                showSecondFragment();
                break;
            case R.id.third_fragment:
                showThirdFragment();
                break;
            case R.id.second_activity:
                showSecondActivity();
                break;
            default:
                break;
        }
    }

    private class REQUEST {
        public static final int SECOND_ACTIVITY = 1000;
    }
}
