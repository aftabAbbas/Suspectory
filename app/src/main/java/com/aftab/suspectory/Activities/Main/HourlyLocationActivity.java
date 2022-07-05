package com.aftab.suspectory.Activities.Main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.aftab.suspectory.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

public class HourlyLocationActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    Toolbar toolbar;
    LocationManager locationManager;
    Context context;
    boolean isGPSEnable = false, isNetworkEnable = false;
    LatLng midLatLng;
    String address, lat, lng;
    GoogleMap mMap;
    TextView tvSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_location);
        initUI();
        clickListener();

    }

    private void initUI() {
        context = HourlyLocationActivity.this;
        toolbar = findViewById(R.id.toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {
            // Functions.requestLocationTurnOn(context);
        }
    }

    private void clickListener() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvSend.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            lat = midLatLng.latitude + "";
            lng = midLatLng.longitude + "";
            returnIntent.putExtra("lat", lat);
            returnIntent.putExtra("lon", lng);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

        mMap.setOnCameraIdleListener(() -> {
            midLatLng = mMap.getCameraPosition().target;
//            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(midLatLng, 15);
//            mMap.animateCamera(location);

            if (midLatLng.latitude != 0 && midLatLng.longitude != 0) {
                try {
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(midLatLng.latitude, midLatLng.longitude, 1);
                    address = addresses.get(0).getAddressLine(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}