package com.aftab.suspectory.Activities.StartUp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.aftab.suspectory.Activities.Main.MainActivity;
import com.aftab.suspectory.Model.Users;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.FireRef;
import com.aftab.suspectory.Utills.Functions;
import com.aftab.suspectory.Utills.SharedPref;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class LoginActivity extends AppCompatActivity {

    Context context;
    SharedPref sh;
    EditText etEmail, etPassword;
    AppCompatButton btnLogin, btnLoginWithGoogle;
    LinearLayout llSignUp;
    String email, password, id, name, dp;
    boolean validate = false;
    FirebaseAuth mAuth;
    FirebaseFirestore fireStore;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
        clickListeners();
    }


    private void initUI() {
        context = LoginActivity.this;
        mAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        sh = new SharedPref(context);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        llSignUp = findViewById(R.id.ll_sign_up);
        btnLoginWithGoogle = findViewById(R.id.btn_login_with_google);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignIn.getClient(context, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                .signOut();

    }


    private void clickListeners() {


        llSignUp.setOnClickListener(v -> startActivity(new Intent(context, SignUpActivity.class)));

        btnLogin.setOnClickListener(v -> {

            validate = validateFields();

            if (validate) {

                allowLogin();

            }

        });

        btnLoginWithGoogle.setOnClickListener(v -> {

            loginWithGoogle();
            //

        });
    }


    private boolean validateFields() {

        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        if (email.equals("")) {

            etEmail.setError(Constants.EMPTY_ERROR);

        } else if (!email.matches(Constants.EMAIL_VALID_REGEX)) {

            etEmail.setError(Constants.NOT_VALID_ADDRESS);

        } else if (password.equals("")) {

            etPassword.setError(Constants.EMPTY_ERROR);

        } else {

            validate = true;
        }


        return validate;
    }

    private void allowLogin() {

        Functions.loadingDialog(context, "Loading", true);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        if (mAuth.getCurrentUser() != null) {

                            id = mAuth.getCurrentUser().getUid();
                            getDataFromFireStore();

                        }


                    } else {


                        if (task.getException() != null) {

                            Functions.showSnackBar(context, task.getException().getMessage());
                            Functions.loadingDialog(context, "Loading", false);

                        }
                    }


                }).addOnFailureListener(e -> {

            Functions.showSnackBar(context, e.getMessage());
            Functions.loadingDialog(context, "Loading", false);

        });

    }

    private void getDataFromFireStore() {

        fireStore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.ID, id).get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {


                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                        sh.putBoolean(Constants.IS_LOGGED_IN, true);
                        sh.putString(Constants.ID, id);
                        sh.putBoolean(Constants.IS_EMAIL_VERIFIED, documentSnapshot.getBoolean(Constants.VERIFIED));
                        sh.putString(Constants.KEY_EMAIL, email);
                        sh.putString(Constants.KEY_FIRST_NAME, documentSnapshot.getString(Constants.KEY_FIRST_NAME));
                        sh.putString(Constants.KEY_LAST_NAME, documentSnapshot.getString(Constants.KEY_LAST_NAME));
                        sh.putString(Constants.PHONE, documentSnapshot.getString(Constants.PHONE));
                        sh.putString(Constants.KEY_DP, documentSnapshot.getString(Constants.KEY_DP));

                        Functions.loadingDialog(context, "Loading", false);
                        Functions.showSnackBar(context, "Logged in");


                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);


                    } else {

                        saveToFireStore();


                    }

                }).addOnFailureListener(e -> {

            Functions.showSnackBar(context, e.getMessage());
            Functions.loadingDialog(context, "Loading", false);
        });


    }


    public void resetPassword(View view) {

        startActivity(new Intent(context, ForgetPasswordActivity.class));


    }


    private void loginWithGoogle() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
        Functions.loadingDialog(context, "Loading", true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                Functions.loadingDialog(context, "Loading", false);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        id = user.getUid();
                        name = user.getDisplayName();
                        email = user.getEmail();
                        dp = Objects.requireNonNull(user.getPhotoUrl()).toString();
                        getDataFromFireStore();

                    } else {
                        Functions.showSnackBar(context, Objects.requireNonNull(task.getException()).getMessage());
                        Functions.loadingDialog(context, "Loading", false);
                    }
                }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Loading", false);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    @SuppressWarnings("deprecation")
    private void saveToFireStore() {


        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {

                String token = task.getResult().getToken();

                String currentString = name;
                String[] separated = currentString.split(" ");

                String firstName = separated[0];
                String lastName = separated[1];

                Users users = new Users(id, firstName, lastName, email, dp, token, "", Constants.GOOGLE, true);

                FireRef.USERS_REF.collection(Constants.KEY_COLLECTION_USERS)
                        .document(id)
                        .set(users)
                        .addOnCompleteListener(task1 -> {


                            sh.putBoolean(Constants.IS_LOGGED_IN, true);
                            sh.putString(Constants.KEY_FIRST_NAME, users.getFirstName());
                            sh.putString(Constants.KEY_LAST_NAME, users.getLastName());
                            sh.putString(Constants.KEY_EMAIL, users.getEmail());
                            sh.putString(Constants.KEY_ID, users.getId());
                            sh.putBoolean(Constants.IS_EMAIL_VERIFIED, true);
                            sh.putString(Constants.SIGNIN_TYPE, Constants.GOOGLE);


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