package com.example.bs148.finderapp;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.bs148.finderapp.constants.SessionManagement;
import com.example.bs148.finderapp.customdialog.CustomAlertDialog;
import com.example.bs148.finderapp.customdialog.CustomProgressDialog;
import com.example.bs148.finderapp.fragment.AddressFragment;
import com.example.bs148.finderapp.others.AddressInformation;
import com.example.bs148.finderapp.others.InternetConnectivity;
import com.example.bs148.finderapp.others.ToolbarSet;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AddressInformation currentAddress;
    CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SessionManagement.authenticationToken="AIzaSyB7bcJUH9MyUazML7nuzA655UC45etzoa0";

        if (isGpsEnables()) {
            getLatLng();
        } else {
            CustomAlertDialog.showSettingsAlert(MainActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        int id = item.getItemId();
        if (id == R.id.nav_address) {
            getLatLng();

        }

        return true;
    }

    private void getLatLng() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog = new CustomProgressDialog(MainActivity.this);
                progressDialog.setProgressDialog();
                LocationTrackerFinal testClass = new LocationTrackerFinal(MainActivity.this);
            }
        }, 400);
    }


    private void isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetWork = connectivityManager.getActiveNetworkInfo();
        if (activeNetWork != null) {
            /*if(activeNetWork.getType()==ConnectivityManager.TYPE_WIFI){
                Toast.makeText(MainActivity.this,"Connected to Wifi",Toast.LENGTH_LONG).show();
            }else if(activeNetWork.getType()==ConnectivityManager.TYPE_MOBILE){
                Toast.makeText(MainActivity.this,"Connected to Network",Toast.LENGTH_LONG).show();
            }*/
        } else {
            Toast.makeText(MainActivity.this, "Please Connect To Internat", Toast.LENGTH_LONG).show();

        }
    }

    private boolean isGpsEnables() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            return false;
            //CustomAlertDialog.showSettingsAlert(MainActivity.this);
        } else {
            return true;
        }
    }

    public void onEvent(InternetConnectivity internetConnectivity) {
        if (internetConnectivity.getmFlag()) {

        } else {
            finish();
        }
    }

    public void onEvent(ToolbarSet toolbarSet) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbarSet.getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void onEvent(final Location location) {
        progressDialog.dismissDialog();
        AddressFragment mapViewFragment = AddressFragment.newInstance(location.getLatitude(), location.getLongitude());
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.layoutContainer, mapViewFragment, mapViewFragment.getTag());
        transaction.commit();
    }

}
