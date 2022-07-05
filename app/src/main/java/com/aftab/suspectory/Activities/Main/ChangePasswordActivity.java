package com.aftab.suspectory.Activities.Main;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.FireRef;
import com.aftab.suspectory.Utills.Functions;
import com.aftab.suspectory.Utills.SharedPref;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {
    Toolbar toolbar;
    Context context;
    EditText etOldPwd, etNewPwd, etConfirmPwd;
    Button btnChange;
    SharedPref sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initUI();
    }

    private void initUI() {
        context = ChangePasswordActivity.this;
        sp = new SharedPref(context);


        viewsInit();
        clickListeners();
    }

    private void viewsInit() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        etOldPwd = findViewById(R.id.et_old_password);
        etNewPwd = findViewById(R.id.et_new_password);
        etConfirmPwd = findViewById(R.id.et_confirm_password);
        btnChange = findViewById(R.id.btn_change);
    }

    private void clickListeners() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        btnChange.setOnClickListener(v -> validateFields());
    }

    private void validateFields() {
        String oldPwd = etOldPwd.getText().toString().trim();
        String newPwd = etNewPwd.getText().toString().trim();
        String cPwd = etConfirmPwd.getText().toString().trim();

        if (oldPwd.isEmpty()) {
            etOldPwd.setError("Please enter your old password");
        } else if (newPwd.isEmpty()) {
            etNewPwd.setError("Please enter your new password");
        } else if (cPwd.isEmpty()) {
            etConfirmPwd.setError("Please enter your confirm password");
        } else {
            if (!newPwd.equals(cPwd)) {
                Functions.showSnackBar(context, "Your entered password didn't match");
            } else {
                if (newPwd.length() < 6 || newPwd.length() > 16) {
                    Functions.showSnackBar(context, "Please enter your password between 6 to 16 characters");
                } else {

                    changePassword(sp.getString(Constants.KEY_EMAIL), oldPwd, newPwd);

                }
            }
        }
    }

    private void changePassword(String email, String oldPwd, String newPwd) {

        Functions.loadingDialog(context, "Loading", true);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPwd);

        assert user != null;
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(newPwd).addOnCompleteListener(task1 -> {

                    if (task1.isSuccessful()) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(Constants.PASSWORD, newPwd);

                        FireRef.USERS_REF
                                .collection(Constants.KEY_COLLECTION_USERS)
                                .document(sp.getString(Constants.ID))
                                .update(hashMap)
                                .addOnCompleteListener(task2 -> {

                                    Functions.loadingDialog(context, "Loading", false);
                                    Functions.showSnackBar(context, "Changed Successfully");

                                }).addOnFailureListener(e -> {

                            Functions.loadingDialog(context, "Loading", false);
                            Functions.showSnackBar(context, e.getMessage());
                        });

                    }
                });
            } else {
                Functions.loadingDialog(context, "Loading", false);
                Functions.showSnackBar(context, task.getException().getMessage());
            }
        });
    }
}