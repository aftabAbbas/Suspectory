package com.aftab.suspectory.Activities.Main;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.SharedPref;

public class NotificationsActivity extends AppCompatActivity {
    Context context;
    Toolbar toolbar;
    SharedPref sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        initUI();
        clickListeners();

    }

    private void initUI() {
        context = NotificationsActivity.this;
        sh = new SharedPref(context);
        toolbar = findViewById(R.id.toolbar);
    }

    private void clickListeners() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


    }
}