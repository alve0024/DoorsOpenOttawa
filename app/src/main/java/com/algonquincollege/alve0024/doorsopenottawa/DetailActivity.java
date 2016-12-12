package com.algonquincollege.alve0024.doorsopenottawa;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class DetailActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder mGeocoder;

    private TextView buildingName;
    private TextView buildingAddress;
    private TextView buildingDescription;
    private TextView buildingOpenHours;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mGeocoder = new Geocoder(this, Locale.getDefault());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildingName = (TextView) findViewById(R.id.buildingName);
        buildingAddress = (TextView) findViewById(R.id.buildingAddress);
        buildingDescription = (TextView) findViewById(R.id.buildingDescription);
        buildingOpenHours = (TextView) findViewById(R.id.buildingOpenHours);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            buildingName.setText(bundle.getString("buildingName"));
            buildingAddress.setText(bundle.getString("buildingAddress"));
            buildingDescription.setText(bundle.getString("buildingDescription"));
            buildingOpenHours.setText(bundle.getString("buildingOpenHours"));

            this.pin(bundle.getString("buildingAddress"));
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.pin(bundle.getString("buildingAddress"));
        }
    }

    /**
     * Locate and pin locationName to the map.
     */
    private void pin(String locationName) {
        try {
            // Get the location of the address provided by the user
            Address address = mGeocoder.getFromLocationName(locationName, 1).get(0);
            // Get the Latitude and Longitude from the address
            LatLng ll = new LatLng(address.getLatitude(), address.getLongitude());
            // Add a marker based on the Latitude and Longitude
            mMap.addMarker(new MarkerOptions().position(ll).title(locationName));
            // Show on the map with Zoom 15.0f which is street level
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15.0f));
        } catch (Exception e) {
            Toast.makeText(this, "Not found: " + locationName, Toast.LENGTH_SHORT).show();
        }
    }

}
