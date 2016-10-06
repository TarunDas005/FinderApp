package com.example.bs148.finderapp.fragment;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bs148.finderapp.R;
import com.example.bs148.finderapp.RefreshCurrentLocationTracker;
import com.example.bs148.finderapp.constants.SessionManagement;
import com.example.bs148.finderapp.customdialog.CustomProgressDialog;
import com.example.bs148.finderapp.model.UserAddressInformation;
import com.example.bs148.finderapp.networkprovider.NetworkServiceHandler;
import com.example.bs148.finderapp.networkprovider.RestClient;
import com.example.bs148.finderapp.others.AddressInformation;
import com.example.bs148.finderapp.others.LocationRefresh;
import com.example.bs148.finderapp.others.ToolbarSet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddressFragment extends BaseFragment {
    CustomProgressDialog progressDialog;
    Toolbar toolbar;
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "logitude";
    private double latitude;
    private double longitude;
    private String fullAddress;
    private String cityName;

    ImageView imageView;
    MapView mMapView;
    private GoogleMap googleMap;
    TextView addressTextView, latitudeTextView, longitudeTextView;

    boolean lockStatus=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            latitude = getArguments().getDouble(LATITUDE);
            longitude = getArguments().getDouble(LONGITUDE);
        }
    }

    public static AddressFragment newInstance(double latitude, double longitude) {
        AddressFragment fragment = new AddressFragment();
        Bundle args = new Bundle();
        args.putDouble(LATITUDE, latitude);
        args.putDouble(LONGITUDE, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location_fragment, container, false);
        addressTextView = (TextView) rootView.findViewById(R.id.addressTextView);
        latitudeTextView = (TextView) rootView.findViewById(R.id.latitudeTextView);
        longitudeTextView = (TextView) rootView.findViewById(R.id.longitudeTextView);
        imageView= (ImageView) rootView.findViewById(R.id.lockImageview);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ToolbarSet toolbarSet = new ToolbarSet(toolbar);
        EventBus.getDefault().post(toolbarSet);
        lockStatus=false;

        setLatitudeAndLongitude();

        if (isNetworkAvailable()) {
            getUrl();
        } else {
            Toast.makeText(getContext(), "Please Connect To Internat", Toast.LENGTH_LONG).show();
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lockStatus){
                    imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.lock));
                    lockStatus=true;
                    googleMap.getUiSettings().setAllGesturesEnabled(false);
                }else{
                    imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.unlock));
                    lockStatus=false;
                    googleMap.getUiSettings().setAllGesturesEnabled(true);
                }

            }
        });
        return rootView;
    }

    private void setLatitudeAndLongitude() {
        if (latitude != 0) {
            latitudeTextView.setText(latitude + "");
        } else {
            latitudeTextView.setText("No Data Found");
        }
        if (longitude != 0) {
            longitudeTextView.setText(longitude + "");
        } else {
            longitudeTextView.setText("No Data Found");
        }
    }

    private void getUrl() {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=" + SessionManagement.authenticationToken;
        NetworkServiceHandler.processPreloginCallBack(RestClient.get().getUserAddressInformation(url), getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void showMarker() {

        googleMap.clear();
        googleMap.getUiSettings().setAllGesturesEnabled(true);

        Circle circle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(latitude, longitude))
                .radius(50)
                .strokeColor(Color.RED));


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //googleMap.setMyLocationEnabled(true);
        LatLng sydney = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(sydney).title(cityName).snippet(fullAddress));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(18).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.unlock));
    }



    private void setRootAndMapView() {

        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                showMarker();

            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.address_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            getCurrentLocation();
            //Toast.makeText(getActivity(), "refresh Selected", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getCurrentLocation() {
        progressDialog = new CustomProgressDialog(getContext());
        progressDialog.setProgressDialog();
        RefreshCurrentLocationTracker testClass = new RefreshCurrentLocationTracker(getActivity());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetWork = connectivityManager.getActiveNetworkInfo();
        if (activeNetWork != null) {
            return true;
            /*if(activeNetWork.getType()==ConnectivityManager.TYPE_WIFI){
                Toast.makeText(MainActivity.this,"Connected to Wifi",Toast.LENGTH_LONG).show();
            }else if(activeNetWork.getType()==ConnectivityManager.TYPE_MOBILE){
                Toast.makeText(MainActivity.this,"Connected to Network",Toast.LENGTH_LONG).show();
            }*/
        } else {
            //Toast.makeText(getContext(),"Please Connect To Internat",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void onEvent(LocationRefresh locationRefresh) {
        progressDialog.dismissDialog();
        latitude=locationRefresh.getLocation().getLatitude();
        longitude=locationRefresh.getLocation().getLongitude();
        setLatitudeAndLongitude();
        if(isNetworkAvailable()){
            getUrl();
        }
        else {
            Toast.makeText(getContext(), "Please Connect To Internat", Toast.LENGTH_LONG).show();
        }
    }

    public void onEvent(UserAddressInformation userAddressInformation) {
        if(userAddressInformation!=null){
            if(userAddressInformation.getResults().get(0).getFormatted_address()!=null)
                fullAddress=userAddressInformation.getResults().get(0).getFormatted_address();
            else
                fullAddress="No Data Found";
            if(userAddressInformation.getResults().get(0).getAddress_components().get(2)!=null){
                cityName=userAddressInformation.getResults().get(0).getAddress_components().get(2).getLong_name();
            }
            setAddressView();
            setRootAndMapView();
        }else{
            Toast.makeText(getContext(),"No Data Found",Toast.LENGTH_LONG).show();
        }
    }

    private void setAddressView() {
        addressTextView.setText(fullAddress);
    }
}
