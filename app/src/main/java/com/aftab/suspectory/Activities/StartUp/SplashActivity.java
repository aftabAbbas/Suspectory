package com.aftab.suspectory.Activities.StartUp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.aftab.suspectory.Activities.Main.MainActivity;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.Functions;
import com.aftab.suspectory.Utills.SharedPref;

@SuppressWarnings("deprecation")
public class SplashActivity extends AppCompatActivity {

    SharedPref sh;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initUI();

    }

    private void initUI() {
        context = SplashActivity.this;
        sh = new SharedPref(context);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Functions.hideSystemUI(this);


        new Handler().postDelayed(() -> {

            if (sh.getBoolean(Constants.IS_LOGGED_IN)) {

                if (!sh.getBoolean(Constants.IS_EMAIL_VERIFIED)) {

                    startActivity(new Intent(context, VerifyEmailActivity.class));


                } else {

                    startActivity(new Intent(context, MainActivity.class));

                }

            } else {

                startActivity(new Intent(context, LoginActivity.class));
            }
            finish();
        }, 1000);
    }
}