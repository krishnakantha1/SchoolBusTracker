package com.krishna.schoolbustracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;

import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS=3;
    public static boolean SELF_GPS_RETRIVAL=true;
    private GoogleMap mMap;
    Handler h = new Handler();
    Handler h2 = new Handler();

    public static final int RESULT_CODE = 99;
    public static final int GOT_LOCATION = 98;
    EditText search;
    Button searchBtn;
    Map<String,Marker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        markers=new HashMap<>();

        search = (EditText) findViewById(R.id.search);
        searchBtn=(Button)findViewById(R.id.searchbtn);

        //Search button
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bus=search.getText().toString();
                if(!bus.isEmpty()){
                    zoomBus(bus);
                }
            }
        });

        SharedPreferences sharedPreferences=getSharedPreferences("com.krishna.schoolbustracker",Context.MODE_PRIVATE);

        //Search bar and button only shown if type=Admin.
        if(!(sharedPreferences.getInt("admin",-1)==1)){
            searchBtn.setVisibility(View.INVISIBLE);
            search.setVisibility(View.INVISIBLE);
        }

        //Ask app permission for location.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            //Service to obtain bus location.
            if(sharedPreferences.getInt("admin",-1)!=2) {
                 Intent i1= new Intent(this, Myservice.class);
                ResultReceiver r = new myReciver(null);
                i1.putExtra("reciver", r);
                startService(i1);
            }
            //service to obtain user location.
            Intent i2 = new Intent(this, GetMyLocation.class);
            ResultReceiver r1 = new myLocation(null);
            i2.putExtra("reciver", r1);
            startService(i2);
        }
        askGPSLocation(this);
    }

    //method to zoom on the bus searched by the admin if present.
    private void zoomBus(String bus) {
        if(markers.containsKey(bus)){
            LatLng ll=markers.get(bus).getPosition();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(ll)
                    .zoom(14.0f).build();
            //Zoom in and animate the camera.
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        else{
            Toast.makeText(this, "Bus number does'nt exist.", Toast.LENGTH_SHORT).show();
        }
    }

    //Method to ask user to activate devise GPS.
    private void askGPSLocation(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        //  Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        //Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Listener for markers on the map.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                SharedPreferences sharedPreferences=getSharedPreferences("com.krishna.schoolbustracker",Context.MODE_PRIVATE);
                if(sharedPreferences.getInt("admin",0)==2){
                    Intent i=new Intent(MapsActivity.this,StudentBusAttendance.class);
                    i.putExtra("busno",marker.getTitle());
                    startActivity(i);
                }else{
                    Intent i=new Intent(MapsActivity.this,BusLoctionInfo.class);
                    startActivity(i);
                }
                return false;
            }
        });
    }

    //Logout
    public void home(View view) {
        SharedPreferences sf=getSharedPreferences("com.krishna.schoolbustracker", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sf.edit();
        editor.remove("email");
        editor.remove("password");
        editor.remove("id");
        editor.commit();
        Intent i=new Intent(MapsActivity.this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    //Handle the location of the different school bus from the Myservice Service.
    public class myReciver extends ResultReceiver{


        public myReciver(Handler handler) {
            super(handler);
        }
        boolean flag=true;
        @Override
        protected void onReceiveResult(int resultCode, final Bundle requestdata){
            if(resultCode==RESULT_CODE && requestdata!=null){
                 final double[] lt=requestdata.getDoubleArray("lat");
                 final double[] lg=requestdata.getDoubleArray("lng");
                 final String[] id1=requestdata.getStringArray("busno");
                 final String[] iconurl=requestdata.getStringArray("url");
                 final int len=requestdata.getInt("leng");
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                                if(flag) {
                                    for (int i = 0; i < len; i++) {
                                        LatLng position = new LatLng(lt[i], lg[i]);
                                        Marker marker=mMap.addMarker(new MarkerOptions().position(position).title(id1[i]));
                                        loadMarkerIcon(marker,iconurl[i]);
                                        markers.put(id1[i],marker);
                                    }
                                    flag=false;
                                }else{
                                    for(int i=0;i<len;i++){
                                        if(!markers.containsKey(id1[i])){
                                            LatLng position = new LatLng(lt[i], lg[i]);
                                            Marker marker=mMap.addMarker(new MarkerOptions().position(position).title("Marker in Sydney"));
                                            loadMarkerIcon(marker,iconurl[i]);
                                            markers.put(id1[i],marker);
                                            continue;
                                        }
                                        LatLng newpos=new LatLng(lt[i],lg[i]);
                                        animateMarker(markers.get(id1[i]),newpos,false);
                                    }
                                }
                        }catch (Exception e){
                            //don't do anything
                        }
                    }
                });
            }
        }

        private void loadMarkerIcon(final Marker marker,String icon) {
            String burlImg = "http://www.thantrajna.com/sjec_01/"+icon;
            Glide.with(MapsActivity.this).asBitmap().load(burlImg).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Bitmap newBit=Bitmap.createScaledBitmap(resource,80,80,false);
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(newBit));
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        }


        public void animateMarker(final Marker marker, final LatLng toPosition,
                                  final boolean hideMarker) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            Projection proj = mMap.getProjection();
            Point startPoint = proj.toScreenLocation(marker.getPosition());
            final LatLng startLatLng = proj.fromScreenLocation(startPoint);
            final long duration = 1000;

            final LinearInterpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed
                            / duration);
                    double lng = t * toPosition.longitude + (1 - t)
                            * startLatLng.longitude;
                    double lat = t * toPosition.latitude + (1 - t)
                            * startLatLng.latitude;
                    marker.setPosition(new LatLng(lat, lng));

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        if (hideMarker) {
                            marker.setVisible(false);
                        } else {
                            marker.setVisible(true);
                        }
                    }
                }
            });
        }
    }
    //Handle the user location obtained from mylocation service.
    public class myLocation extends ResultReceiver{
        double lat,lng,plat,plng;
        boolean once=true;
        Marker myloc;
        public myLocation(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle requestdata){
            if(resultCode==GOT_LOCATION && requestdata!=null){
                h2.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            lat = requestdata.getDouble("lat");
                            lng = requestdata.getDouble("lng");
                            Toast.makeText(MapsActivity.this, lat+"  "+lng, Toast.LENGTH_SHORT).show();
                            LatLng myLocation1 = new LatLng(lat, lng);
                            if (once){
                                myloc=mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation1, 14.0f));
                                once=false;
                                plat=lat;
                                plng=lng;
                            }else if(lat!=plat || lng!=plng){
                                animateMarker(myloc,new LatLng(lat,lng),false);
                                plat=lat;
                                plng=lng;
                            }
                        }catch (Exception e){
                            Toast.makeText(MapsActivity.this, "Error Getting Location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }
        public void animateMarker(final Marker marker, final LatLng toPosition,
                                  final boolean hideMarker) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            Projection proj = mMap.getProjection();
            Point startPoint = proj.toScreenLocation(marker.getPosition());
            final LatLng startLatLng = proj.fromScreenLocation(startPoint);
            final long duration = 1000;

            final LinearInterpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed
                            / duration);
                    double lng = t * toPosition.longitude + (1 - t)
                            * startLatLng.longitude;
                    double lat = t * toPosition.latitude + (1 - t)
                            * startLatLng.latitude;
                    marker.setPosition(new LatLng(lat, lng));

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        if (hideMarker) {
                            marker.setVisible(false);
                        } else {
                            marker.setVisible(true);
                        }
                    }
                }
            });
        }
    }
}
