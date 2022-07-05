package com.aftab.suspectory.Activities.Main;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.aftab.suspectory.Adapter.Recyclerview.SuspectedContactsAdapter;
import com.aftab.suspectory.Model.CallLogs;
import com.aftab.suspectory.Model.Chat;
import com.aftab.suspectory.Model.SimSMS;
import com.aftab.suspectory.Model.SuspectedContacts;
import com.aftab.suspectory.Model.WhatsAppMessage;
import com.aftab.suspectory.Model.WhatsappCallLogs;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.FireRef;
import com.aftab.suspectory.Utills.Functions;
import com.aftab.suspectory.Utills.MySwipeHelper;
import com.aftab.suspectory.Utills.SharedPref;
import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SuspectedContactsActivity extends AppCompatActivity {
    Context context;
    RecyclerView rvAllContacts;
    ImageView ivSearch;
    ArrayList<SuspectedContacts> contactsList = new ArrayList<>();
    ArrayList<SuspectedContacts> filteredContactsList = new ArrayList<>();
    SuspectedContactsAdapter allContactsAdapter;
    MaterialSearchView simpleSearchView;
    Toolbar toolbar;
    SharedPref sh;
    String userDeviceId;
    MySwipeHelper mySwipeHelper;
    int mainCount = 0, whatsappCount = 0, callLogsCount = 0, simCount = 0, waCount = 0, wvCount;
    long lastWhatsappMilli, lastCallMilli, lastSimMilli;
    LottieAnimationView lavNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suspected_contacts);
        initUI();
        searchListener();
        clickListeners();
        swipeHelper();


    }




    private void initUI() {
        context = SuspectedContactsActivity.this;
        sh = new SharedPref(context);
        toolbar = findViewById(R.id.toolbar);
        rvAllContacts = findViewById(R.id.rv_all_contacts);
        ivSearch = findViewById(R.id.iv_search);
        simpleSearchView = findViewById(R.id.search_view);
        lavNoData = findViewById(R.id.lav_no_data);

        setSupportActionBar(toolbar);


        userDeviceId = sh.getString(Constants.DEVICE_ID);

        FireRef.SUSPECTED_CONTACTS.child(userDeviceId).keepSynced(true);


    }

    private void clickListeners() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ivSearch.setOnClickListener(v -> {

            simpleSearchView.showSearch();
            //
        });


    }
    private void getAllContacts() {

        Functions.loadingDialog(context, "Loading", true);

        FireRef.SUSPECTED_CONTACTS.child(userDeviceId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        contactsList.clear();

                        mainCount = whatsappCount = callLogsCount = simCount = 0;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            SuspectedContacts contacts = dataSnapshot.getValue(SuspectedContacts.class);
                            contactsList.add(contacts);

                        }

                        Collections.sort(contactsList, (o1, o2) -> o1.getName().compareTo(o2.getName()));

                        if (contactsList.size() > 0) {

                            mainCount = 0;
                            getCallLogsCount();

                        } else {

                            setAdapter();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Functions.loadingDialog(context, "Loading", false);
                        Functions.showSnackBar(context, error.getMessage());
                    }
                });

    }

    private void getCallLogsCount() {

        FireRef.CALL_LOGS.child(userDeviceId).child(contactsList.get(mainCount).getNumber().trim()).keepSynced(true);

        FireRef.CALL_LOGS.child(userDeviceId)
                .child(contactsList.get(mainCount).getNumber().trim())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        callLogsCount = 0;
                        lastCallMilli = 0;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            CallLogs callLogs = dataSnapshot.getValue(CallLogs.class);

                            assert callLogs != null;
                            Chat chat = new Chat(callLogs.getId(), "", callLogs.getCallDate(),
                                    callLogs.getCallType(), callLogs.getCallDuration(), "", "", "", callLogs.isRead());

                            if (!chat.getIsRead()) {

                                callLogsCount++;

                            }


                            lastCallMilli = Long.parseLong(chat.getTime());

                            if (lastCallMilli < Long.parseLong(chat.getTime())) {

                                lastCallMilli = Long.parseLong(chat.getTime());

                            }

                        }

                        getSIMSMS();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Functions.loadingDialog(context, "Loading", false);
                        Functions.showSnackBar(context, error.getMessage());
                    }
                });

    }

    private void getSIMSMS() {

        FireRef.SIM_SMS.child(userDeviceId).child(contactsList.get(mainCount).getNumber().trim()).keepSynced(true);


        FireRef.SIM_SMS.child(userDeviceId)
                .child(contactsList.get(mainCount).getNumber().trim())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        simCount = 0;
                        lastSimMilli = 0;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            SimSMS simSMS = dataSnapshot.getValue(SimSMS.class);

                            assert simSMS != null;
                            Chat chat = new Chat(simSMS.getId(), simSMS.getMsg(), simSMS.getTime(), simSMS.getType()
                                    , "", "", simSMS.getLocation(), "", simSMS.isRead());

                            if (!chat.getIsRead()) {

                                simCount++;

                            }


                            lastSimMilli = Long.parseLong(chat.getTime());

                            if (lastSimMilli < Long.parseLong(chat.getTime())) {

                                lastSimMilli = Long.parseLong(chat.getTime());

                            }

                        }


                        getWhatsappWALog();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Functions.loadingDialog(context, "Loading", false);
                        Functions.showSnackBar(context, error.getMessage());
                    }
                });
    }

    private void getWhatsappWALog() {

        FireRef.WhATSAPP_A_CALL_LOG.child(userDeviceId).child(contactsList.get(mainCount).getNumber().trim()).keepSynced(true);


        FireRef.WhATSAPP_A_CALL_LOG.child(userDeviceId)
                .child(contactsList.get(mainCount).getNumber().trim())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        waCount = 0;
                        lastSimMilli = 0;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            WhatsappCallLogs whatsappCallLogs = dataSnapshot.getValue(WhatsappCallLogs.class);

                            assert whatsappCallLogs != null;
                            Chat chat = new Chat(whatsappCallLogs.getId(), "", whatsappCallLogs.getCallDate(),
                                    whatsappCallLogs.getCallType(), whatsappCallLogs.getCallDuration(), "",
                                    whatsappCallLogs.getLocation(), whatsappCallLogs.getUrl(), whatsappCallLogs.isRead());


                            if (!chat.getIsRead()) {

                                waCount++;

                            }


                            lastSimMilli = Long.parseLong(chat.getTime());

                            if (lastSimMilli < Long.parseLong(chat.getTime())) {

                                lastSimMilli = Long.parseLong(chat.getTime());

                            }

                        }


                        getWhatsappWVLog();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Functions.loadingDialog(context, "Loading", false);
                        Functions.showSnackBar(context, error.getMessage());
                    }
                });

    }

    private void getWhatsappWVLog() {

        FireRef.WhATSAPP_V_CALL_LOG.child(userDeviceId).child(contactsList.get(mainCount).getNumber().trim()).keepSynced(true);


        FireRef.WhATSAPP_V_CALL_LOG.child(userDeviceId)
                .child(contactsList.get(mainCount).getNumber().trim())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        wvCount = 0;
                        lastSimMilli = 0;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            WhatsappCallLogs whatsappCallLogs = dataSnapshot.getValue(WhatsappCallLogs.class);

                            assert whatsappCallLogs != null;
                            Chat chat = new Chat(whatsappCallLogs.getId(), "", whatsappCallLogs.getCallDate(),
                                    whatsappCallLogs.getCallType(), whatsappCallLogs.getCallDuration(), "",
                                    whatsappCallLogs.getLocation(), whatsappCallLogs.getUrl(), whatsappCallLogs.isRead());


                            if (!chat.getIsRead()) {

                                wvCount++;

                            }


                            lastSimMilli = Long.parseLong(chat.getTime());

                            if (lastSimMilli < Long.parseLong(chat.getTime())) {

                                lastSimMilli = Long.parseLong(chat.getTime());

                            }

                        }


                        getWhatsappMessage();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Functions.loadingDialog(context, "Loading", false);
                        Functions.showSnackBar(context, error.getMessage());
                    }
                });
    }

    private void getWhatsappMessage() {

        FireRef.WhATSAPP_MESSAGE.child(userDeviceId).child(contactsList.get(mainCount).getNumber().trim()).keepSynced(true);

        FireRef.WhATSAPP_MESSAGE.child(userDeviceId)
                .child(contactsList.get(mainCount).getNumber().trim())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        whatsappCount = 0;
                        lastWhatsappMilli = 0;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            WhatsAppMessage whatsAppMessage = dataSnapshot.getValue(WhatsAppMessage.class);

                            assert whatsAppMessage != null;
                            Chat chat = new Chat(whatsAppMessage.getId(), whatsAppMessage.getMsg(), whatsAppMessage.getTime()
                                    , whatsAppMessage.getType(), "", "", whatsAppMessage.getLocation(), "", whatsAppMessage.isRead());

                            if (!chat.getIsRead()) {

                                whatsappCount++;

                            }


                            lastWhatsappMilli = Long.parseLong(chat.getTime());

                            if (lastWhatsappMilli < Long.parseLong(chat.getTime())) {

                                lastWhatsappMilli = Long.parseLong(chat.getTime());

                            }
                        }

                        mainCount++;


                        if (mainCount <= contactsList.size()) {

                            long lastTimeNoti;

                            int finalCount;
                            finalCount = callLogsCount + simCount + whatsappCount;

                            if (finalCount > 99) {

                                contactsList.get(mainCount - 1).setUnReadCount("99+");


                            } else {

                                contactsList.get(mainCount - 1).setUnReadCount(finalCount + "");

                            }

                            lastTimeNoti = Math.max(lastSimMilli, lastCallMilli);

                            contactsList.get(mainCount - 1).setLastNoti(lastTimeNoti + "");


                            if (mainCount < contactsList.size()) {

                                getCallLogsCount();

                            } else {

                                //  rvAllContacts.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

                                setAdapter();

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Functions.loadingDialog(context, "Loading", false);
                        Functions.showSnackBar(context, error.getMessage());
                    }
                });
    }

    private void setAdapter() {



        Collections.sort(contactsList, (o1, o2) -> o2.getLastNoti().compareTo(o1.getLastNoti()));


        if (contactsList.size() > 0) {

            lavNoData.setVisibility(View.GONE);

        } else {

            lavNoData.setVisibility(View.VISIBLE);

        }

        Functions.loadingDialog(context, "Loading", false);

        allContactsAdapter = new SuspectedContactsAdapter(context, contactsList);
        rvAllContacts.setAdapter(allContactsAdapter);

    }

    private void searchListener() {

        simpleSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {

                filteredContactsList.clear();

                if (!search.equals("")) {

                    for (SuspectedContacts contacts : contactsList) {

                        String name = contacts.getName().toLowerCase();

                        if (name.contains(search)) {

                            filteredContactsList.add(contacts);

                        }
                        //something here
                    }
                    allContactsAdapter = new SuspectedContactsAdapter(context, filteredContactsList);
                    // intentList.contains(search);
                } else {

                    allContactsAdapter = new SuspectedContactsAdapter(context, contactsList);

                }

                rvAllContacts.setAdapter(allContactsAdapter);


                return false;
            }
        });

    }

    private void swipeHelper() {
        mySwipeHelper = new MySwipeHelper(this, rvAllContacts, 300) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(context, "Delete", R.drawable.ic_delete_red,
                        40,
                        Color.parseColor("#ffffff"),

                        pos -> {
                            openRemoveToSuspectedDialog(pos);

                        }));
            }
        };
    }

    private void openRemoveToSuspectedDialog(int position) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.remove_suspected_dialog);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button btnYes = dialog.findViewById(R.id.btn_yes);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);


        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnYes.setOnClickListener(v -> {

            dialog.dismiss();
            removeFromSuspected(contactsList.get(position));

        });


        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void removeFromSuspected(SuspectedContacts contacts) {

        Functions.loadingDialog(context, "Deleting", true);

        FireRef.SUSPECTED_CONTACTS
                .child(userDeviceId)
                .child(contacts.getNumber())
                .removeValue()
                .addOnCompleteListener(task -> {

                   // rvAllContacts.setAdapter(allContactsAdapter);
                   // swipeHelper();

                    Functions.loadingDialog(context, "Deleting", false);
                    Functions.showSnackBar(context, "Deleted Successfully");

                    getAllContacts();


                }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Deleting", false);
            Functions.showSnackBar(context, e.getMessage());

        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllContacts();
    }
}