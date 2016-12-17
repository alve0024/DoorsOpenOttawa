/*********************************************************************************************
 *   Doors Open Ottawa - List information about the Buildings with the Doors Open            *
 *                                                                                           *
 *   @author Leonardo Alps (alve0024@algonquinlive.com)                                      *
 *                                                                                           *
 *   Supervision: Gerald.Hurdle@AlgonquinCollege.com                                         *
 *                                                                                           *
 *   Algonquin College - All right reserved!                                                 *
 *                                                                                           *
 *********************************************************************************************/

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
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


/**
 * DetailActivity only displays more details of a particular building selected from
 * MainActivity's ListView.
 *
 * - Shows Name, Address, Description and Open Hour
 * - Shows a map indicating the location of the building in the map
 */
public class DetailActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder mGeocoder;

    private TextView mBuildingNameTextView;
    private TextView mBuildingAddressTextView;
    private TextView mBuildingDescriptionTextView;
    private TextView mBuildingOpenHoursTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mGeocoder = new Geocoder(this, Locale.getDefault());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mBuildingNameTextView = (TextView) findViewById(R.id.buildingName);
        mBuildingAddressTextView = (TextView) findViewById(R.id.buildingAddress);
        mBuildingDescriptionTextView = (TextView) findViewById(R.id.buildingDescription);
        mBuildingOpenHoursTextView = (TextView) findViewById(R.id.buildingOpenHours);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mBuildingNameTextView.setText(bundle.getString("buildingName"));
            mBuildingAddressTextView.setText(bundle.getString("buildingAddress"));
            mBuildingDescriptionTextView.setText(bundle.getString("buildingDescription"));
            mBuildingOpenHoursTextView.setText(bundle.getString("buildingOpenHours"));

            this.pin(bundle.getString("buildingAddress"));
        }

    }

    /**
     * Callback interface for when the map is ready to be used.
     * @param googleMap A non-null instance of a GoogleMap associated with the MapFragment or
     *                  MapView that defines the callback.
     */
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
