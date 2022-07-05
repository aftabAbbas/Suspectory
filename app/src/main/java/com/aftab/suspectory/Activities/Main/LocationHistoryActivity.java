package com.aftab.suspectory.Activities.Main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aftab.suspectory.Model.Chat;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class LocationHistoryActivity extends AppCompatActivity implements OnMapReadyCallback {

    Context context;
    Toolbar toolbar;
    ArrayList<Chat> chatArrayList = new ArrayList<>();
    List<LatLng> latLngsList = new ArrayList<>();
    Polyline polyline;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        initUI();


    }

    @SuppressLint("InflateParams")
    private void initUI() {

        context = LocationHistoryActivity.this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        chatArrayList = (ArrayList<Chat>) getIntent().getSerializableExtra(Constants.CHAT);


        for (int i = 0; i < chatArrayList.size(); i++) {

            Chat chat = chatArrayList.get(i);

            String latLngStr = chat.getLocation();

            if (!
                    (latLngStr.equals("No Location") || latLngStr.equals("0.0,0.0"))) {

                String lat = latLngStr.split(",")[latLngStr.split(",").length - 1];
                String lon = latLngStr.substring(0, latLngStr.length() - lat.length());

                lat = lat.replace(",", "");
                lon = lon.replace(",", "");


                double lat1 = Double.parseDouble(lat);
                double lon1 = Double.parseDouble(lon);
                LatLng latLng = new LatLng(lat1, lon1);

                latLngsList.add(latLng);
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (latLngsList.size() > 0) {

            drawPolyLineOnMap(latLngsList);

        } else {

            Toast.makeText(context, "No Location found!", Toast.LENGTH_SHORT).show();
        }

    }

    public void drawPolyLineOnMap(List<LatLng> list) {


        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.RED);
        polyOptions.width(10);
        polyOptions.addAll(list);

        polyline = mMap.addPolyline(polyOptions);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        for (LatLng latLng : list) {
            builder.include(latLng);
        }

        final LatLngBounds bounds = builder.build();
        //BOUND_PADDING is an int to specify padding of bound.. try 100.
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 10);
        mMap.animateCamera(cu);
    }

}