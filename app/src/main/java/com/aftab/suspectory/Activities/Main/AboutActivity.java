package com.aftab.suspectory.Activities.Main;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aftab.suspectory.Model.Reports;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.Functions;
import com.aftab.suspectory.Utills.SharedPref;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thekhaeng.pushdownanim.PushDownAnim;

public class AboutActivity extends AppCompatActivity {
    Context context;
    Toolbar toolbar;
    SharedPref sharedPref;
    EditText etName, etEmail, etDesc;
    Button btnSend;
    DatabaseReference myRef;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        init();
    }

    private void init() {
        context = AboutActivity.this;
        sharedPref = new SharedPref(context);
        //currentUserId = sharedPref.get(Constants.CURRENT_USER_ID);

        viewsInit();
        clickListeners();
    }

    private void viewsInit() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        etName = findViewById(R.id.tv_user_name);
        etEmail = findViewById(R.id.et_email);
        etDesc = findViewById(R.id.et_description);
        btnSend = findViewById(R.id.btn_send);

        PushDownAnim.setPushDownAnimTo(btnSend).setScale(PushDownAnim.MODE_SCALE, 0.95f);
    }

    private void clickListeners() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        btnSend.setOnClickListener(v -> getProblemDetailFromUser());
    }

    private void getProblemDetailFromUser() {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String desc = etDesc.getText().toString();

        if (name.isEmpty()) {
            etName.setError(getString(R.string.please_enter_your_name));
            return;
        }
        if (email.isEmpty()) {
            etEmail.setError(getString(R.string.enter_your_email));
            return;
        }
        if (desc.isEmpty()) {
            etDesc.setError(getString(R.string.enter_your_desc));
            return;
        }
        if (!Functions.isValidEmailAddress(email)) {
            etEmail.setError(getString(R.string.enter_valid_email));
        } else {
            sendReport(name, email, desc);
        }
    }

    private void sendReport(String name, String email, String desc) {
        myRef = FirebaseDatabase.getInstance().getReference(Constants.KEY_REF_REPORT).child(currentUserId);
        Reports reports = new Reports(name, email, desc);
        myRef.setValue(reports);
        Toast.makeText(context, getString(R.string.report_problem_submission), Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

}