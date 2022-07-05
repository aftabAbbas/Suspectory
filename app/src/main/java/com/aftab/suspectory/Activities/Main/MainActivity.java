package com.aftab.suspectory.Activities.Main;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.aftab.suspectory.Activities.StartUp.LoginActivity;
import com.aftab.suspectory.Adapter.Spinner.SuspectedPersonAdapter;
import com.aftab.suspectory.Model.DeviceInfo;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.FireRef;
import com.aftab.suspectory.Utills.SharedPref;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Context context;
    ImageView ivNotifications;
    SharedPref sh;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    Spinner spin;
    CardView cvSuspectedContacts, cvUnknownContacts, cvAllContacts;
    ArrayList<DeviceInfo> deviceInfoList = new ArrayList<>();
    CircleImageView civUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        clickListeners();



    }

    private void initUI() {
        context = MainActivity.this;
        sh = new SharedPref(context);
        ivNotifications = findViewById(R.id.iv_notification);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        spin = findViewById(R.id.sp_lead_source);
        cvSuspectedContacts = findViewById(R.id.cv_suspected_contacts);
        cvUnknownContacts = findViewById(R.id.cv_unknown_contacts);
        cvAllContacts = findViewById(R.id.cv_all_contacts);
        civUser = findViewById(R.id.civ_dp);

        navigationView.setItemIconTintList(null);
        spin.setOnItemSelectedListener(this);

        navigationSet();


    }

    @Override
    protected void onResume() {
        super.onResume();
        headerSetting();

        getDevices();

    }

    private void getDevices() {

        FireRef.DEVICES
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        deviceInfoList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            DeviceInfo deviceInfo = dataSnapshot.getValue(DeviceInfo.class);
                            deviceInfoList.add(deviceInfo);

                        }


                        SuspectedPersonAdapter customAdapter = new SuspectedPersonAdapter(context, deviceInfoList);
                        spin.setAdapter(customAdapter);

                        String selectedPosition = sh.getString(Constants.SELECTED_DEVICE_POSITION);

                        if (!selectedPosition.equals("")) {

                            spin.setSelection(Integer.parseInt(selectedPosition));

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void clickListeners() {

        ivNotifications.setOnClickListener(v -> {

            startActivity(new Intent(context, NotificationsActivity.class));
            //

        });

        toggle.setToolbarNavigationClickListener(v -> {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        cvSuspectedContacts.setOnClickListener(v -> {

            startActivity(new Intent(context, SuspectedContactsActivity.class));
            //
        });

        cvUnknownContacts.setOnClickListener(v -> {

            startActivity(new Intent(context, UnknownContactsActivity.class));
            //
        });

        cvAllContacts.setOnClickListener(v -> {

            startActivity(new Intent(context, AllContactsActivity.class));
            //
        });


    }

    private void navigationSet() {

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {

                drawerLayout.closeDrawer(GravityCompat.START);

            } else if (item.getItemId() == R.id.menu_settings) {

                drawerLayout.closeDrawer(GravityCompat.START);

                startActivity(new Intent(context, SettingsActivity.class));

            } else if (item.getItemId() == R.id.menu_about) {

                drawerLayout.closeDrawer(GravityCompat.START);

                startActivity(new Intent(context, AboutActivity.class));

            } else if (item.getItemId() == R.id.menu_logout) {

                drawerLayout.closeDrawer(GravityCompat.START);
                showLogoutDialog();

            }
            return false;
        });

    }

    private void headerSetting() {

        View headerView = navigationView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tv_name);
        TextView tvEmail = headerView.findViewById(R.id.tv_email);
        CircleImageView cvUser = headerView.findViewById(R.id.civ_user);

        String name = sh.getString(Constants.KEY_FIRST_NAME) + " " + sh.getString(Constants.KEY_LAST_NAME);
        String email = sh.getString(Constants.KEY_EMAIL);

        tvName.setText(name);
        tvEmail.setText(email);
        Glide.with(context).load(sh.getString(Constants.KEY_DP)).placeholder(R.drawable.place_holder).into(cvUser);
        Glide.with(context).load(sh.getString(Constants.KEY_DP)).placeholder(R.drawable.place_holder).into(civUser);


    }


    private void showLogoutDialog() {

        Dialog logoutDialog = new Dialog(context);
        logoutDialog.setContentView(R.layout.logout_confirm_dialog);
        Objects.requireNonNull(logoutDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        logoutDialog.setCancelable(false);

        Button btnYes = logoutDialog.findViewById(R.id.btn_yes);
        Button btnNo = logoutDialog.findViewById(R.id.btn_no);


        btnYes.setOnClickListener(v -> {

            logoutDialog.dismiss();

            logout();

        });

        btnNo.setOnClickListener(v -> logoutDialog.dismiss());

        logoutDialog.show();
        logoutDialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

    }

    private void logout() {

        FirebaseAuth.getInstance().signOut();
        sh.clearPrefrence();


        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String deviceId = deviceInfoList.get(position).getDeviceId();

        sh.putString(Constants.DEVICE_ID, deviceId);
        sh.putString(Constants.SELECTED_DEVICE_POSITION, position + "");


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}