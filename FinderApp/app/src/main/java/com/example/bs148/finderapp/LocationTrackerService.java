package com.example.bs148.finderapp;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.example.bs148.finderapp.others.LocationAvailable;

/**
 * Created by BS148 on 10/4/2016.
 */

public class LocationTrackerService extends Service  {
    private Location currentLocation;
    private LocationManager locationManager;
    Context context;
    boolean canGetLocation = false;
    double latitude; // latitude
    double longitude; // longitude

    public LocationTrackerService(Context context) {
        this.context = context;
        getLocation();
    }

    public void getLocation(){

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gpsEnabled && !isNetworkEnabled) {
        }else{
            this.canGetLocation=true;
            Location gpsLocation = null;
            Location networkLocation = null;
            // age remove kore nilam
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return ;
            }
            locationManager.removeUpdates(listener);
            gpsLocation = requestUpdateFromProvider(LocationManager.GPS_PROVIDER);
            networkLocation = requestUpdateFromProvider(LocationManager.NETWORK_PROVIDER);

            if (gpsLocation != null && networkLocation != null) {
                //find better one
                Location myLocation = getBetterLocation(gpsLocation, networkLocation);
                setLocation(myLocation);
            } else if (gpsLocation != null) {
                setLocation(gpsLocation);
                //use gps location
            } else if (networkLocation != null) {
                //use networki location
                setLocation(networkLocation);
            } else {
                LocationAvailable locationAvailable=new LocationAvailable(false);
            }
        }
    }

    private void setLocation(Location location) {
        currentLocation=location;
    }

    private Location getBetterLocation(Location newLocation, Location currentBestLocation) {
        //return best one
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return newLocation;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > 60000;//1 min
        boolean isSignificantlyOlder = timeDelta < 60000;
        boolean isNewer = timeDelta > 0;

        // If it's been more than one minutes since the current location, use
        // the new location
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return newLocation;
            // If the new location is more than one minutes older, it must be
            // worse
        } else if (isSignificantlyOlder) {
            return currentBestLocation;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation
                .getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;

        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return newLocation;
        } else if (isNewer && !isLessAccurate) {
            return newLocation;
        }
        return currentBestLocation;
    }

    private Location requestUpdateFromProvider(String provider) {
        Location location = null;
        //jodi enable thake then amra req korbo.
        if (locationManager.isProviderEnabled(provider)) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            locationManager.requestLocationUpdates(provider, 0, 0, listener);
            location = locationManager.getLastKnownLocation(provider);
        } else {
            Toast.makeText(getApplicationContext(), provider + " is not enabled", Toast.LENGTH_LONG).show();
        }
        return location;
    }

    LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }
    public double getLatitude(){
        if(currentLocation != null){
            latitude = currentLocation.getLatitude();
        }

        return latitude;
    }

    public double getLongitude(){
        if(currentLocation != null){
            longitude = currentLocation.getLongitude();
        }

        return longitude;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(listener);
    }
}
