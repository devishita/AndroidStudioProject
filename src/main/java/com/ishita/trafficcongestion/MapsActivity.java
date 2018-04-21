package com.ishita.trafficcongestion;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    boolean mLocationPermissionGranted=false;
    static public final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=1;
    LatLng userLocation, latLng;
    Location myLocation;
    Button btn;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        updateLocationUI();
        Intent in = getIntent();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn=(Button)findViewById(R.id.blocation);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MapsActivity.this,BookSlotActivity.class);
                startActivity(i);
            }
        });

    }

    public void onSearch(View view)
    {
        EditText location_tf=(EditText)findViewById(R.id.destination);
        String location =location_tf.getText().toString();
        List<Address> addressList=null;

        if(location!=null || location.equals(""))
        {
            Geocoder geocoder=new Geocoder(this);
            try {
                addressList=geocoder.getFromLocationName(location,1);
                Toast.makeText(getBaseContext(),addressList.toString(),Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(addressList!=null && addressList.size()!=0) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                Double lat = Double.valueOf(address.getLatitude());
                Double lng = Double.valueOf(address.getLongitude());

                float[] results = new float[1];
                Location.distanceBetween(lat, lng, myLocation.getLatitude(), myLocation.getLongitude(), results);
                //float distance = myLocation.distanceTo(latLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title("Distance = " + results[0]);
                markerOptions.title("Duration = " + results[0]);
                mMap.addMarker(markerOptions);
            }
        }
    }


 private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
        //onMapReady(GoogleMap map);
    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        // Add a marker in Sydney and move the camera
       // LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).draggable(true).title("Marker"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(0,0));
       // mMap.setMyLocationEnabled(true);
        updateLocationUI();
        //getDeviceLocation();

    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                //mMap.setMyLocationEnabled(true);
                LocationManager lm=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
                Location myLocation=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(myLocation==null) {
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    String provider=lm.getBestProvider(criteria,true);
                    myLocation=lm.getLastKnownLocation(provider);

                }
                if(myLocation!=null)
                {
                    LatLng userLocation = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userLocation).draggable(true).title("Marker"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation,14),1500,null);

                }

            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
               // mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}
