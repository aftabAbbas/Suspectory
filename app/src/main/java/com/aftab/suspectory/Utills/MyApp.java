package com.aftab.suspectory.Utills;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        FireRef.CONTACTS.keepSynced(true);
        FireRef.CALL_LOGS.keepSynced(true);
        FireRef.SIM_SMS.keepSynced(true);
        FireRef.SUSPECTED_CONTACTS.keepSynced(true);
        FireRef.WhATSAPP_MESSAGE.keepSynced(true);

    }

}
