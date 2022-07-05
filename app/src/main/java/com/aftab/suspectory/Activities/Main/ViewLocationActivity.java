package com.aftab.suspectory.Activities.Main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aftab.suspectory.Model.Chat;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.Functions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    Context context;
    Marker marker;
    Chat chatIntent;
    Toolbar toolbar;
    String lat, lon, dp, latLngStr;
    TextView tvName, tvTime, tvAddress;
    private GoogleMap mMap;
    private View mCustomMarkerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        initUI();


    }

    @SuppressLint("InflateParams")
    private void initUI() {

        context = ViewLocationActivity.this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        mCustomMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        tvName = mCustomMarkerView.findViewById(R.id.tv_name);
        tvTime = mCustomMarkerView.findViewById(R.id.tv_time);
        tvAddress = mCustomMarkerView.findViewById(R.id.tv_loc_name);


        chatIntent = (Chat) getIntent().getSerializableExtra(Constants.CHAT);


        latLngStr = chatIntent.getLocation();


        lat = latLngStr.split(",")[latLngStr.split(",").length - 1];
        lon = latLngStr.substring(0, latLngStr.length() - lat.length());

        lat = lat.replace(",", "");
        lon = lon.replace(",", "");


        String address = Functions.getAddress(context, lat, lon);

        String timeDate = Functions.getDateTime(chatIntent.getTime());

        timeDate = "Time: " + timeDate;

        tvTime.setText(timeDate);
        tvAddress.setText(address);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));


        addCustomMarker(latLng);


    }

    private void addCustomMarker(final LatLng latLng) {

        // adding a marker with image from URL using glide image loading library

        // Bitmap bitmap = drawableToBitmap(resource);
        Bitmap image;


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView)));
        marker = mMap.addMarker(markerOptions);
        // marker.showInfoWindow();
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(location);


    }


    private Bitmap getMarkerBitmapFromView(View view) {

        //   mMarkerImageView.setImageBitmap(bitmap);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;


    }

}