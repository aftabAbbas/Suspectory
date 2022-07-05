package com.aftab.suspectory.Activities.Main;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.aftab.suspectory.Model.DeviceInfo;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.FireRef;
import com.aftab.suspectory.Utills.LoadingDialog;
import com.aftab.suspectory.Utills.SharedPref;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

public class AddDevicesActivity extends AppCompatActivity {

    Context context;
    RecyclerView rvDevices;
    FloatingActionButton fabAddDevice;
    ImageView ivSearch, ivFilter;
    MaterialSearchView simpleSearchView;
    Toolbar toolbar;
    String userDeviceId;
    SharedPref sh;
    ArrayList<DeviceInfo> deviceInfoList = new ArrayList<>();
    LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_devices);
        initUI();
        clickListeners();
    }


    private void initUI() {
        context = AddDevicesActivity.this;
        sh = new SharedPref(context);
        rvDevices = findViewById(R.id.rv_devices);
        fabAddDevice = findViewById(R.id.floatingActionButton);
        ivSearch = findViewById(R.id.iv_search);
        ivFilter = findViewById(R.id.iv_filter);
        simpleSearchView = findViewById(R.id.search_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        userDeviceId = sh.getString(Constants.DEVICE_ID);
    }


    private void clickListeners() {

        ivSearch.setOnClickListener(v -> {

            simpleSearchView.showSearch();
            //
        });

        fabAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openSearchDialog();

            }
        });

    }

    private void openSearchDialog() {



    }

    @Override
    protected void onResume() {
        super.onResume();
        getAddedDevices();
    }

    private void getAddedDevices() {

        loadingDialog = new LoadingDialog(context, "Loading");
        loadingDialog.show();

        FireRef.ADDED_DEVICES.child(sh.getString(Constants.ID))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        deviceInfoList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            DeviceInfo deviceInfo = dataSnapshot.getValue(DeviceInfo.class);
                            deviceInfoList.add(deviceInfo);

                        }

                        if (deviceInfoList.size() > 0) {


                        } else {

                            //tvNoDevices.setVisibility(View.VISIBLE);


                        }

                        loadingDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}