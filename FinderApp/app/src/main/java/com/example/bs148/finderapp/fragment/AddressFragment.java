package com.example.bs148.finderapp.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.text.Html;
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
import com.example.bs148.finderapp.constants.LatLngValue;
import com.example.bs148.finderapp.constants.SessionManagement;
import com.example.bs148.finderapp.customdialog.CustomProgressDialog;
import com.example.bs148.finderapp.model.UserAddressInformation;
import com.example.bs148.finderapp.networkprovider.NetworkServiceHandler;
import com.example.bs148.finderapp.networkprovider.RestClient;
import com.example.bs148.finderapp.others.AddressInformation;
import com.example.bs148.finderapp.others.LocationRefresh;
import com.example.bs148.finderapp.others.ToolbarSet;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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
import java.util.Locale;

import de.greenrobot.event.EventBus;

import static android.app.Activity.RESULT_OK;
import static com.example.bs148.finderapp.constants.ResponseCode.PLACE_PICKER_REQUEST;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddressFragment extends BaseFragment {
    private final static String BUNDLE_KEY_MAP_STATE = "mapData";
    CustomProgressDialog progressDialog;
    Toolbar toolbar;
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "logitude";
    private String fullAddress;
    private String cityName;
    private String snippetMessage;

    ImageView imageView;
    MapView mMapView;
    private GoogleMap googleMap;
    TextView addressTextView, latitudeTextView, longitudeTextView;

    boolean lockStatus = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            LatLngValue.lattitde = getArguments().getDouble(LATITUDE);
            LatLngValue.longitude = getArguments().getDouble(LONGITUDE);
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
        imageView = (ImageView) rootView.findViewById(R.id.lockImageview);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);

        Bundle mapState = null;
        if (savedInstanceState != null) {
            // Load the map state bundle from the main savedInstanceState
            mapState = savedInstanceState.getBundle(BUNDLE_KEY_MAP_STATE);
        }
        mMapView.onCreate(mapState);
        //mMapView.onCreate(savedInstanceState);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ToolbarSet toolbarSet = new ToolbarSet(toolbar);
        EventBus.getDefault().post(toolbarSet);
        lockStatus = false;

        setLatitudeAndLongitude();

        if (isNetworkAvailable()) {
            getUrl();
        } else {
            Toast.makeText(getContext(), "Please Connect To Internat", Toast.LENGTH_LONG).show();
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lockStatus) {
                    imageView.setImageResource(R.drawable.lock);
                    lockStatus = true;
                    googleMap.getUiSettings().setAllGesturesEnabled(false);
                } else {
                    imageView.setImageResource(R.drawable.unlock);
                    lockStatus = false;
                    googleMap.getUiSettings().setAllGesturesEnabled(true);
                }

            }
        });
        return rootView;
    }

    private void setLatitudeAndLongitude() {
        if (LatLngValue.lattitde != 0) {
            latitudeTextView.setText(LatLngValue.lattitde + "");
        } else {
            latitudeTextView.setText("No Data Found");
        }
        if (LatLngValue.longitude != 0) {
            longitudeTextView.setText(LatLngValue.longitude + "");
        } else {
            longitudeTextView.setText("No Data Found");
        }
    }

    private void getUrl() {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + LatLngValue.lattitde + "," + LatLngValue.longitude + "&key=" + SessionManagement.authenticationToken;
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
                .center(new LatLng(LatLngValue.lattitde, LatLngValue.longitude))
                .radius(50)
                .strokeColor(Color.RED));


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //googleMap.setMyLocationEnabled(true);
        LatLng sydney = new LatLng(LatLngValue.lattitde, LatLngValue.longitude);
        googleMap.addMarker(new MarkerOptions().position(sydney).title(cityName).snippet(snippetMessage));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(18).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        imageView.setImageResource(R.drawable.unlock);
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
            return true;
        }
        if (id == R.id.action_search_location) {
            getManualLocation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getManualLocation() {
        PlacePicker.IntentBuilder builder=new PlacePicker.IntentBuilder();
        try {
            Intent intent=builder.build(getActivity());
            startActivityForResult(intent,PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
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
        LatLngValue.lattitde = locationRefresh.getLocation().getLatitude();
        LatLngValue.longitude = locationRefresh.getLocation().getLongitude();
        setLatitudeAndLongitude();
        if (isNetworkAvailable()) {
            getUrl();
        } else {
            Toast.makeText(getContext(), "Please Connect To Internat", Toast.LENGTH_LONG).show();
        }
    }
    public void onEvent(UserAddressInformation userAddressInformation) {
        /*if (userAddressInformation != null) {
            if (userAddressInformation.getResults().get(0).getFormatted_address() != null) {
                fullAddress = userAddressInformation.getResults().get(0).getFormatted_address();

            } else
                fullAddress = "No Data Found";
            if(userAddressInformation.getResults().get(0).getAddress_components().get(2)!=null){
                cityName=userAddressInformation.getResults().get(0).getAddress_components().get(2).getLong_name();
            }
            setAddressView();
            setRootAndMapView();
        }else{
            Toast.makeText(getContext(),"No Data Found",Toast.LENGTH_LONG).show();
        }*/
        String createManualAddress = "";
        if (userAddressInformation != null) {
            if (userAddressInformation.getResults().get(0).getFormatted_address() != null) {
                fullAddress = userAddressInformation.getResults().get(0).getFormatted_address();
                snippetMessage=fullAddress;
                if(userAddressInformation.getResults().get(0).getAddress_components().get(2)!=null){
                    cityName=userAddressInformation.getResults().get(0).getAddress_components().get(2).getLong_name();
                }
                if (userAddressInformation.getResults().get(0).getAddress_components() != null) {
                    int size = userAddressInformation.getResults().get(0).getAddress_components().size();
                    int flag = 0;

                    int sizeForSublocality = userAddressInformation.getResults().size();
                    for (int k = 0; k < sizeForSublocality; k++) {
                        int addressComponentSize = userAddressInformation.getResults().get(k).getAddress_components().size();
                        for (int i = 0; i < addressComponentSize; i++) {
                            List<String> types = userAddressInformation.getResults().get(k).getAddress_components().get(i).getTypes();
                            int typeSize = types.size();
                            for (int j = 0; j < typeSize; j++) {
                                if (types.get(j).equals("sublocality_level_1")) {
                                    flag = 1;
                                    createManualAddress += " <font color=#cc0029>Area:</font> " + userAddressInformation.getResults().get(k).getAddress_components().get(i).getLong_name();
                                    break;
                                }
                            }
                            if (flag == 1)
                                break;
                        }
                        if (flag == 1)
                            break;
                    }
                    flag = 0;
                    for (int i = 0; i < size; i++) {
                        List<String> types = userAddressInformation.getResults().get(0).getAddress_components().get(i).getTypes();
                        int typeSize = types.size();
                        for (int j = 0; j < typeSize; j++) {
                            if (types.get(j).equals("administrative_area_level_2")) {
                                flag = 1;
                                createManualAddress += " <font color=#cc0029>District: </font>" + userAddressInformation.getResults().get(0).getAddress_components().get(i).getLong_name();
                                break;
                            }
                        }
                        if (flag == 1)
                            break;
                    }
                    flag = 0;
                    for (int i = 0; i < size; i++) {
                        List<String> types = userAddressInformation.getResults().get(0).getAddress_components().get(i).getTypes();
                        int typeSize = types.size();
                        for (int j = 0; j < typeSize; j++) {
                            if (types.get(j).equals("administrative_area_level_1")) {
                                flag = 1;
                                createManualAddress += " <font color=#cc0029>State: </font>" + userAddressInformation.getResults().get(0).getAddress_components().get(i).getLong_name();
                                break;
                            }
                        }
                        if (flag == 1)
                            break;
                    }

                }

                fullAddress=fullAddress+createManualAddress;
            } else{
                fullAddress = "No Data Found";
                snippetMessage=fullAddress;
            }

            setAddressView();
            setRootAndMapView();
        } else {
            Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_LONG).show();
        }

    }


    private void setAddressView() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            addressTextView.setText(Html.fromHtml((fullAddress),Html.FROM_HTML_MODE_LEGACY));
        } else {
            addressTextView.setText(Html.fromHtml(fullAddress));
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save the map state to it's own bundle
        Bundle mapState = new Bundle();
        mMapView.onSaveInstanceState(mapState);
        // Put the map bundle in the main outState
        outState.putBundle(BUNDLE_KEY_MAP_STATE, mapState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PLACE_PICKER_REQUEST){
            if(resultCode==RESULT_OK){
                Place place=PlacePicker.getPlace(getContext(),data);
                String address=String.format("Place: %s",place.getAddress());
                String placeName= (String) place.getName();
                LatLng latLng=place.getLatLng();

                LatLngValue.lattitde=latLng.latitude;
                LatLngValue.longitude=latLng.longitude;
                fullAddress=placeName+"<br>"+address;

                if(placeName!=null){
                    snippetMessage=placeName;
                }else{
                    snippetMessage=address;
                }

                setLatitudeAndLongitude();
                setAddressView();
                setRootAndMapView();

            }
        }
    }
}
