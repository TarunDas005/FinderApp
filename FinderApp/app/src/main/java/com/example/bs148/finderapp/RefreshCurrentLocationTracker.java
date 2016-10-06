package com.example.bs148.finderapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.example.bs148.finderapp.customdialog.CustomAlertDialog;
import com.example.bs148.finderapp.others.LocationRefresh;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import de.greenrobot.event.EventBus;

/**
 * Created by BS148 on 10/5/2016.
 */

public class RefreshCurrentLocationTracker implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Context context;
    Location currentLocation;
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public RefreshCurrentLocationTracker(Context context) {
        this.context = context;
        if(isGpsEnables()){
            buildGoogleApiClient();
        }else{
            CustomAlertDialog.showSettingsAlert(context);
        }

    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100); // Update location every second


        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            this.currentLocation=mLastLocation;
        }
    }
    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


    }

    public Location getCurrentLocation() {
        return this.currentLocation;
    }

    @Override
    public void onLocationChanged(Location location) {

        if(location!=null){
            mGoogleApiClient.disconnect();
            LocationRefresh locationRefresh=new LocationRefresh(location);
            EventBus.getDefault().post(locationRefresh);
        }
    }

    private boolean isGpsEnables() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            return false;
            //CustomAlertDialog.showSettingsAlert(MainActivity.this);
        } else {
            return true;
        }
    }
}
