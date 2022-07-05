package com.aftab.suspectory.Activities.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.SharedPref;

public class SettingsActivity extends AppCompatActivity {

    Context context;
    SharedPref sh;
    Toolbar toolbar;
    TextView tvEditProfile, tvChangePassword, tvTermsAndCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initUI();
        clickListeners();
    }


    private void initUI() {
        context = SettingsActivity.this;
        sh = new SharedPref(context);
        tvEditProfile = findViewById(R.id.tv_edit_profile);
        tvChangePassword = findViewById(R.id.tv_change_password);
        tvTermsAndCondition = findViewById(R.id.tv_terms_and_condition);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    private void clickListeners() {

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvEditProfile.setOnClickListener(v -> startActivity(new Intent(context, EditProfileActivity.class)));

        tvChangePassword.setOnClickListener(v -> {
                    String signInType = sh.getString(Constants.SIGNIN_TYPE);

                    if (signInType.equals(Constants.KEY_EMAIL)) {

                        startActivity(new Intent(context, ChangePasswordActivity.class));

                    } else {

                        Toast.makeText(context, "Your'e SignIn with Google, you can not change password!", Toast.LENGTH_SHORT).show();

                    }

                }
        );

    }
}