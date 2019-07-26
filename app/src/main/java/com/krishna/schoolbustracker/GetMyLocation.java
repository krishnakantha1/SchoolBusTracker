package com.krishna.schoolbustracker;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;


public class GetMyLocation extends IntentService {

    private FusedLocationProviderClient mFusedLocationClient;
    ResultReceiver rr;
    double lattitude=0, longitude=0;
    LocationRequest locationRequest;

    @Override
    public void onCreate() {
        super.onCreate();
        //required for FusedLocationProviderClient.
        mFusedLocationClient = new FusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(2000);
        locationRequest.setInterval(4000);

    }


    public GetMyLocation(Context context) {
        super("Sample");
    }

    public GetMyLocation() {
        super("Sample");

    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {


        //permission check required
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        //get the location of the user using the FusedLocationProviderClient api.
        mFusedLocationClient.requestLocationUpdates(locationRequest,new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                lattitude = locationResult.getLastLocation().getLatitude();
                longitude = locationResult.getLastLocation().getLongitude();
                rr= intent.getParcelableExtra("reciver");
                Bundle b = new Bundle();
                b.putDouble("lat", lattitude);
                b.putDouble("lng", longitude);
                rr.send(MapsActivity.GOT_LOCATION, b);

            }
        } , getMainLooper());
    }



}
