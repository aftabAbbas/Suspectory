package com.aftab.suspectory.Activities.StartUp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.FireRef;
import com.aftab.suspectory.Utills.Functions;
import com.aftab.suspectory.Utills.SharedPref;
import com.aftab.suspectory.Utills.Validation;

public class SignUpActivity extends AppCompatActivity {
    Context context;
    SharedPref sh;
    EditText etFName, etLName, etEmail, etCPassword, etPassword;
    TextView tvPasswordValidator, tvCPasswordValidator;
    AppCompatButton btnSignUp;
    String firstName, lastName, email, CPassword, password;
    boolean valid = false;
    LinearLayout llLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initUI();
        clickListeners();
    }


    private void initUI() {
        context = SignUpActivity.this;
        sh = new SharedPref(context);
        etFName = findViewById(R.id.et_fName);
        etLName = findViewById(R.id.et_lName);
        etEmail = findViewById(R.id.et_email);
        etCPassword = findViewById(R.id.et_cPassword);
        etPassword = findViewById(R.id.et_password);
        btnSignUp = findViewById(R.id.btn_signUp);
        tvPasswordValidator = findViewById(R.id.tv_password_validator);
        tvCPasswordValidator = findViewById(R.id.tv_cPassword_validator);
        llLogin = findViewById(R.id.ll_login);


    }


    private void clickListeners() {

        btnSignUp.setOnClickListener(v -> {

            firstName = etFName.getText().toString().trim();
            lastName = etLName.getText().toString().trim();
            email = etEmail.getText().toString().trim();
            CPassword = etCPassword.getText().toString().trim();
            password = etPassword.getText().toString().trim();


            valid = fieldsValidation();

            boolean bothPasswordMatch = password.equals(CPassword);


            if (valid && bothPasswordMatch) {

                Functions.loadingDialog(context, "Loading", true);

                validateEmail();


            }

        });

        llLogin.setOnClickListener(v -> {

            startActivity(new Intent(context, LoginActivity.class));
            finish();


        });
    }


    private Boolean fieldsValidation() {


        if (firstName.equals("")) {

            etFName.setError(Constants.EMPTY_ERROR);

        } else if (lastName.equals("")) {

            etLName.setError(Constants.EMPTY_ERROR);

        } else if (email.equals("")) {

            etEmail.setError(Constants.EMPTY_ERROR);

        } else if (!Validation.isValidEmail(email)) {

            etEmail.setError(getString(R.string.enter_valid_email));

        } else if (password.equals("")) {

            etPassword.setError(Constants.EMPTY_ERROR);

        } else if (CPassword.equals("")) {

            etCPassword.setError(Constants.EMPTY_ERROR);

        } else if (password.length() < 6) {

            Toast.makeText(context, "Week password", Toast.LENGTH_SHORT).show();

        } else if (!password.equals(CPassword)) {

            Toast.makeText(context, "Both password are not same", Toast.LENGTH_SHORT).show();

        } else {

            valid = true;
        }


        return valid;

    }


    private void validateEmail() {


        FireRef.USERS_REF.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email.trim())
                .get().addOnCompleteListener(task -> {


            if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {

                Functions.loadingDialog(context, "Loading", false);
                Functions.showSnackBar(context, getString(R.string.user_another_email));

            } else {

                saveDataToSP();
            }

        }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Loading", false);
            Functions.showSnackBar(context, e.getMessage());

        });

    }

    private void saveDataToSP() {


        sh.putBoolean(Constants.IS_LOGGED_IN, true);
        sh.putBoolean(Constants.IS_EMAIL_VERIFIED, false);
        sh.putString(Constants.KEY_FIRST_NAME, firstName);
        sh.putString(Constants.KEY_LAST_NAME, lastName);
        sh.putString(Constants.KEY_EMAIL, email);
        sh.putString(Constants.PASSWORD, CPassword);



        Intent intent = new Intent(context, VerifyEmailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


    }


}