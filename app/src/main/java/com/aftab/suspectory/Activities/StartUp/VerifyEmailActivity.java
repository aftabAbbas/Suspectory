package com.aftab.suspectory.Activities.StartUp;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aftab.suspectory.Activities.Main.MainActivity;
import com.aftab.suspectory.Model.Users;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.FireRef;
import com.aftab.suspectory.Utills.Functions;
import com.aftab.suspectory.Utills.SharedPref;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.goodiebag.pinview.Pinview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerifyEmailActivity extends AppCompatActivity {

    SharedPref sh;
    Context context;
    RequestQueue rq;
    String code;
    Button btnDone;
    Pinview pinview;
    FirebaseFirestore fireStore;
    FirebaseAuth mAuth;
    TextView tvResend;
    DatabaseReference dataRef;
    String id, firstName, lastName, email, phone, token = "", password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        initUI();
    }

    private void initUI() {

        context = VerifyEmailActivity.this;
        mAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        dataRef = FirebaseDatabase.getInstance().getReference();
        sh = new SharedPref(context);
        btnDone = findViewById(R.id.btn_done);
        pinview = findViewById(R.id.pinView);
        tvResend = findViewById(R.id.textView25);

        firstName = sh.getString(Constants.KEY_FIRST_NAME);
        lastName = sh.getString(Constants.KEY_LAST_NAME);
        email = sh.getString(Constants.KEY_EMAIL);
        password = sh.getString(Constants.PASSWORD);

        code = Functions.getRandomRequestCode() + "";

        Functions.hideKeyBoard(context);

        rq = Volley.newRequestQueue(this);

        if (!sh.getBoolean(Constants.IS_EMAILSEND)) {

            Functions.loadingDialog(context, "Sending", true);

            sendMail();


        } else {

            openDialog();

        }

        btnDone.setOnClickListener(v -> {

            Functions.hideKeyBoard(context);

            String savedCode = sh.getString(Constants.CODE);
            String inputtedCode = pinview.getValue();

            if (savedCode.equals(inputtedCode)) {

                Functions.loadingDialog(context, "Matching", true);

                allowSignUp();


            } else {

                Functions.showSnackBar(context, "Not Matched");

            }

        });

        tvResend.setOnClickListener(view -> {

            Functions.loadingDialog(context, "Sending", true);

            sendMail();


        });

        btnDone.setOnClickListener(v -> {

            Functions.hideKeyBoard(context);

            String savedCode = sh.getString(Constants.CODE);
            String inputtedCode = pinview.getValue();

            if (savedCode.equals(inputtedCode)) {

                Functions.loadingDialog(context, "Matching", true);

                allowSignUp();


            } else {

                Functions.showSnackBar(context, "Not Matched");

            }
        });

    }

    private void openDialog() {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.email_send_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView tvText = dialog.findViewById(R.id.tv_email_text);
        Button btnOpenEmail = dialog.findViewById(R.id.btn_open_email);
        ImageView ivCancel = dialog.findViewById(R.id.iv_cancel);

        String text = "Email send to your "
                + "<b>" + sh.getString(Constants.KEY_EMAIL) + "</b> email successfully";


        tvText.setText(Html.fromHtml(text));


        btnOpenEmail.setOnClickListener(v -> {

            dialog.dismiss();

            try {

                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                startActivity(intent);

            } catch (android.content.ActivityNotFoundException e) {

                Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show();

            }

        });

        ivCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
    }

   /* public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        sh.clearPrefrence();


        Intent intent = new Intent(context, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }*/

    private void sendMail() {
        StringRequest request = new StringRequest(Request.Method.POST, Constants.MAIL_URL,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        // String success = jsonObject.getString("success");
                        String message = jsonObject.getString("message");

                        Functions.loadingDialog(context, "Sending", false);

                        sh.putBoolean(Constants.IS_EMAILSEND, true);
                        sh.putString(Constants.CODE, code);

                        Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Functions.loadingDialog(context, "Sending", false);
                        Functions.showSnackBar(context, e.getMessage());

                    }
                }, error -> {

            if (error.getMessage().equals("null")) {

                tvResend.performClick();

            } else {

                Functions.loadingDialog(context, "Sending", false);
                Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();

                String message = "Hey " + sh.getString(Constants.KEY_NAME) + ", you're almost ready to start enjoying Suspectory. Simply Copy this code " + code + " and paste in Suspectory App";
                map.put("to", sh.getString(Constants.KEY_EMAIL));
                map.put("subject", "Suspectory Email Verification");
                map.put("message", message);
                return map;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        rq.add(request);

    }

    public void logout(View view) {

        FirebaseAuth.getInstance().signOut();
        sh.clearPrefrence();


        Intent intent = new Intent(context, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }


    private void allowSignUp() {


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {


                        id = mAuth.getCurrentUser().getUid();
                        saveToFireStore();

                    }

                }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Matching", false);
            Functions.showSnackBar(context, e.getMessage());


        });

    }

    @SuppressWarnings("deprecation")
    private void saveToFireStore() {


        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {

                token = task.getResult().getToken();

                Users users = new Users(id, firstName, lastName, email, "", token, password, Constants.KEY_EMAIL, true);

                FireRef.USERS_REF.collection(Constants.KEY_COLLECTION_USERS)
                        .document(id)
                        .set(users)
                        .addOnCompleteListener(task1 -> {


                            sh.putBoolean(Constants.IS_LOGGED_IN, true);
                            sh.putString(Constants.KEY_FIRST_NAME, users.getFirstName());
                            sh.putString(Constants.KEY_LAST_NAME, users.getLastName());
                            sh.putString(Constants.KEY_EMAIL, users.getEmail());
                            sh.putString(Constants.KEY_ID, users.getId());
                            sh.putString(Constants.SIGNIN_TYPE, Constants.KEY_EMAIL);
                            sh.putBoolean(Constants.IS_EMAIL_VERIFIED, true);


                            Functions.loadingDialog(context, "Matching", false);
                            Functions.showSnackBar(context, "Matched");


                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);


                        }).addOnFailureListener(e -> {

                    Functions.loadingDialog(context, "Matching", false);
                    Functions.showSnackBar(context, e.getMessage());


                });

            }

        });


    }
}