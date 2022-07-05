package com.aftab.suspectory.Activities.StartUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.aftab.suspectory.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
    }

    public void createPassword(View view) {
        startActivity(new Intent(ForgetPasswordActivity.this, CreatePasswordActivity.class));
    }
}