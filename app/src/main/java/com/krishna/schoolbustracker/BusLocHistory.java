package com.krishna.schoolbustracker;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class BusLocHistory extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng pos;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_loc_history);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        pos=getIntent().getParcelableExtra("latlng");
        title=getIntent().getExtras().getString("title");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(pos).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(pos)
                .zoom(14.0f).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
