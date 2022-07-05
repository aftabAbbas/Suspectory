package com.aftab.suspectory.Activities.Main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.FireRef;
import com.aftab.suspectory.Utills.Functions;
import com.aftab.suspectory.Utills.SharedPref;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    Context context;
    SharedPref sh;
    Toolbar toolbar;
    EditText etFName, etLName, etEmail;
    CircleImageView civUser;
    TextView tvEditFName, tvEditLName, tvEditEmail, tvEditPassword;
    String fName, lName, email, dp;
    Button btnUpdate, btnUpload;
    ImageView ivChoseImage;
    Uri selectedFileURI = null;
    StorageReference userDpStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initUI();
        clickListeners();
        setUi();
    }


    private void initUI() {
        context = EditProfileActivity.this;
        sh = new SharedPref(context);
        userDpStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");
        etFName = findViewById(R.id.et_fName);
        etLName = findViewById(R.id.et_lName);
        etEmail = findViewById(R.id.et_email);
        civUser = findViewById(R.id.civ_user);
        tvEditFName = findViewById(R.id.tv_edit_first_name);
        tvEditLName = findViewById(R.id.tv_edit_last_name);
        tvEditEmail = findViewById(R.id.tv_edit_email);
        tvEditPassword = findViewById(R.id.tv_edit_pwd);
        civUser = findViewById(R.id.civ_user);
        ivChoseImage = findViewById(R.id.iv_chose_image);
        btnUpdate = findViewById(R.id.btn_update);
        btnUpload = findViewById(R.id.btn_upload);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void clickListeners() {

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvEditFName.setOnClickListener(v -> {

            etLName.setEnabled(false);
            etFName.setEnabled(true);

            btnUpdate.setVisibility(View.VISIBLE);
            Functions.showKeyBoard(context, etFName);

        });


        tvEditLName.setOnClickListener(v -> {

            etLName.setEnabled(true);
            etFName.setEnabled(false);

            btnUpdate.setVisibility(View.VISIBLE);
            Functions.showKeyBoard(context, etLName);

        });


        btnUpdate.setOnClickListener(v -> {

            updateInfo(true);
            //
        });

        ivChoseImage.setOnClickListener(v -> {
            choseImage();
            //
        });


        btnUpload.setOnClickListener(v -> {
            uploadDpToDatabase();
            //
        });

        tvEditPassword.setOnClickListener(v -> {

            String signInType = sh.getString(Constants.SIGNIN_TYPE);

            if (signInType.equals(Constants.KEY_EMAIL)) {

                startActivity(new Intent(context, ChangePasswordActivity.class));

            } else {

                Toast.makeText(context, "Your'e SignIn with Google, you can not change password!", Toast.LENGTH_SHORT).show();

            }

        });

        tvEditEmail.setOnClickListener(v -> {

            String signInType = sh.getString(Constants.SIGNIN_TYPE);

            if (signInType.equals(Constants.KEY_EMAIL)) {

               showChangeEmailDialog();

            } else {

                Toast.makeText(context, "Your'e SignIn with Google, you can not change email!", Toast.LENGTH_SHORT).show();

            }

        });

    }

    private void choseImage() {
        ImagePicker.Companion.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)
                .start();//Final image resolution will be less than 1080 x 1080(Optional)

    }

    private void setUi() {

        fName = sh.getString(Constants.KEY_FIRST_NAME);
        lName = sh.getString(Constants.KEY_LAST_NAME);
        email = sh.getString(Constants.KEY_EMAIL);
        dp = sh.getString(Constants.KEY_DP);

        etFName.setText(fName);
        etLName.setText(lName);
        etEmail.setText(email);

        Glide.with(context).load(dp).placeholder(R.drawable.place_holder).into(civUser);

    }


    private void updateInfo(boolean b) {

        if (b) {

            Functions.loadingDialog(context, "Uploading", true);
        }


        fName = etFName.getText().toString().trim();
        lName = etLName.getText().toString().trim();

        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put(Constants.KEY_FIRST_NAME, fName);
        userInfo.put(Constants.KEY_LAST_NAME, lName);
        userInfo.put(Constants.KEY_DP, dp);

        FireRef.USERS_REF
                .collection(Constants.KEY_COLLECTION_USERS)
                .document(sh.getString(Constants.ID))
                .update(userInfo)
                .addOnCompleteListener(task -> {

                    sh.putString(Constants.KEY_FIRST_NAME, fName);
                    sh.putString(Constants.KEY_LAST_NAME, lName);
                    sh.putString(Constants.KEY_DP, dp);

                    setUi();

                    Functions.loadingDialog(context, "Uploading", false);
                    Functions.showSnackBar(context, "Updated");
                    btnUpdate.setVisibility(View.GONE);
                    btnUpload.setVisibility(View.GONE);

                    etFName.setEnabled(false);
                    etLName.setEnabled(false);

                    Functions.hideKeyBoard(context);

                }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Uploading", false);
            Functions.showSnackBar(context, e.getMessage());

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (data != null) {
                selectedFileURI = data.getData();

                if (selectedFileURI != null && !Objects.requireNonNull(selectedFileURI.getPath()).isEmpty()) {

                    btnUpdate.setVisibility(View.GONE);
                    civUser.setImageURI(selectedFileURI);
                    Glide.with(context).load(selectedFileURI).into(civUser);
                    btnUpload.setVisibility(View.VISIBLE);

                } else {

                    Functions.showSnackBar(context, "Cannot Get this Image");

                }
            }

        }
    }

    private void uploadDpToDatabase() {

        Functions.loadingDialog(context, "Uploading", true);

        StorageReference filePath = userDpStorageRef.child(sh.getString(Constants.ID) + ".jpg");

        filePath.putFile(selectedFileURI).addOnSuccessListener(taskSnapshot -> {

            final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

            firebaseUri.addOnSuccessListener(uri -> {

                dp = uri.toString();
                updateInfo(false);

            }).addOnFailureListener(e -> {

                Functions.loadingDialog(context, "Uploading", false);
                Functions.showSnackBar(context, e.getMessage());

            });

        }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Uploading", false);
            Functions.showSnackBar(context, e.getMessage());

        });


    }

    private void showChangeEmailDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.change_email_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogScale;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

        EditText etDialogOldEmail = dialog.findViewById(R.id.et_old_email);
        EditText etDialogEmail = dialog.findViewById(R.id.et_email);
        EditText etDialogPassword = dialog.findViewById(R.id.et_password);
        Button btnUpdate = dialog.findViewById(R.id.btn_save);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        dialog.show();

        etDialogOldEmail.setText(sh.getString(Constants.KEY_EMAIL));
        etDialogOldEmail.setFocusableInTouchMode(false);

        btnUpdate.setOnClickListener(v -> {

            String email = etDialogEmail.getText().toString().trim();
            String oldEmail = etDialogOldEmail.getText().toString().trim();
            String pwd = etDialogPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etDialogEmail.setError("Please enter your new email address!");

            } else if (pwd.isEmpty()) {
                etDialogPassword.setError("Please enter your password!");

            } else {

                if (Functions.isValidEmailAddress(oldEmail)) {
                    if (Functions.isValidEmailAddress(email)) {

                        if (pwd.length() < 6 || pwd.length() > 16) {
                            Toast.makeText(context, "Please enter password between 6 to 16 digits!", Toast.LENGTH_SHORT).show();
                        } else {


                            if (pwd.equals(sh.getString(Constants.PASSWORD))) {

                                changeEmail(email, oldEmail, pwd, dialog);

                            } else {

                                dialog.dismiss();
                                Toast.makeText(context, getString(R.string.your_entered_email_or_pwd_is_incorrect), Toast.LENGTH_SHORT).show();
                            }


                        }

                    } else {
                        etDialogEmail.setError(getString(R.string.enter_valid_email));
                    }

                } else {
                    etDialogOldEmail.setError(getString(R.string.enter_valid_email));
                }
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }


    private void changeEmail(String email, String oldEmail, String password, Dialog dialog) {
        if (!email.equals(oldEmail)) {

            dialog.dismiss();

            Functions.loadingDialog(context, "Loading", true);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, password);

            if (user != null) {
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();

                    if (user1 != null) {
                        user1.updateEmail(email).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put(Constants.KEY_EMAIL, email);

                                FireRef.USERS_REF
                                        .collection(Constants.KEY_COLLECTION_USERS)
                                        .document(sh.getString(Constants.ID))
                                        .update(hashMap)
                                        .addOnCompleteListener(task2 -> {

                                            sh.putString(Constants.KEY_EMAIL,email);

                                            Functions.loadingDialog(context, "Loading", false);
                                            Functions.showSnackBar(context, "Your email has been changed successfully");

                                        }).addOnFailureListener(e -> {

                                            Functions.loadingDialog(context, "Loading", false);
                                            Functions.showSnackBar(context, e.getMessage());


                                        });


                            } else {
                                Functions.loadingDialog(context, "Loading", false);
                                Functions.showSnackBar(context, getString(R.string.your_email_or_pwd_is_wrong));
                            }
                        });
                    }
                });
            }
        } else {
            Toast.makeText(context, "Please enter different email", Toast.LENGTH_SHORT).show();
        }
    }
}
