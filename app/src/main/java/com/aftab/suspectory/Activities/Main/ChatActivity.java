package com.aftab.suspectory.Activities.Main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftab.suspectory.Adapter.Recyclerview.ChatAdapter;
import com.aftab.suspectory.Model.CallLogs;
import com.aftab.suspectory.Model.Chat;
import com.aftab.suspectory.Model.Contacts;
import com.aftab.suspectory.Model.SimSMS;
import com.aftab.suspectory.Model.SuspectedContacts;
import com.aftab.suspectory.Model.WhatsAppMessage;
import com.aftab.suspectory.Model.WhatsappCallLogs;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.FireRef;
import com.aftab.suspectory.Utills.Functions;
import com.aftab.suspectory.Utills.SharedPref;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ALL")
public class ChatActivity extends AppCompatActivity {


    Context context;
    TextView tvFirstChar, tvName, tvMusicTime, tvTotalTime;
    SuspectedContacts suspectedContacts;
    Contacts contactsIntent;
    LinearLayout layoutBack, llNoChat;
    String userDeviceId;
    String name, number, color, fChar;
    ArrayList<Chat> chatArrayList = new ArrayList<>();
    ArrayList<Chat> filterdArrayList = new ArrayList<>();
    ChatAdapter chatAdapter;
    RecyclerView rvChat;
    SharedPref sh;
    ImageView ivFilter, ivLocation, ivPlayPause, ivRecType;
    String sortBy = Constants.ALL, total;
    MediaPlayer mediaPlayer;
    SeekBar sbMusic;
    int totalTime;
    ProgressBar pbMusic;
    Handler handler = new Handler();
    boolean isPlaying = false;
    BottomSheetDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initUI();
        clickListeners();


    }


    private void initUI() {
        context = ChatActivity.this;
        sh = new SharedPref(context);
        tvFirstChar = findViewById(R.id.tv_f_char);
        tvName = findViewById(R.id.tv_user_name);
        layoutBack = findViewById(R.id.layout_back);
        rvChat = findViewById(R.id.rv_chat);
        ivFilter = findViewById(R.id.iv_filter);
        ivLocation = findViewById(R.id.iv_location);
        llNoChat = findViewById(R.id.ll_no_chat);

        String from = getIntent().getStringExtra(Constants.FROM);

        if (from.equals(Constants.SUSPECTED_CONTACTS)) {

            suspectedContacts = (SuspectedContacts) getIntent().getSerializableExtra(Constants.CONTACTS);

            name = suspectedContacts.getName();
            number = suspectedContacts.getNumber();
            color = suspectedContacts.getColor();
        } else {

            contactsIntent = (Contacts) getIntent().getSerializableExtra(Constants.CONTACTS);

            name = contactsIntent.getName();
            number = contactsIntent.getNumber();
            color = contactsIntent.getColor();

        }


        getWindow().setSharedElementEnterTransition(Functions.enterTransition());
        getWindow().setSharedElementReturnTransition(Functions.returnTransition());

        userDeviceId = sh.getString(Constants.DEVICE_ID);
        setData();

        FireRef.CONTACTS.child(userDeviceId).child(number.trim()).keepSynced(true);
        FireRef.CALL_LOGS.child(userDeviceId).child(number.trim()).keepSynced(true);
        FireRef.SIM_SMS.child(userDeviceId).child(number.trim()).keepSynced(true);
        FireRef.SUSPECTED_CONTACTS.child(userDeviceId).child(number.trim()).keepSynced(true);
        FireRef.WhATSAPP_MESSAGE.child(userDeviceId).child(number.trim()).keepSynced(true);
        FireRef.WhATSAPP_A_CALL_LOG.child(userDeviceId).child(number.trim()).keepSynced(true);
        FireRef.WhATSAPP_V_CALL_LOG.child(userDeviceId).child(number.trim()).keepSynced(true);


    }

    private void getSIMSMS() {

        Functions.loadingDialog(context, "Loading", true);


        FireRef.SIM_SMS.child(userDeviceId)
                .child(number.trim())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        chatArrayList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            SimSMS simSMS = dataSnapshot.getValue(SimSMS.class);

                            assert simSMS != null;
                            Chat chat = new Chat(simSMS.getId(), simSMS.getMsg(), simSMS.getTime(), simSMS.getType(), "", "", simSMS.getLocation(), "no", simSMS.isRead());
                            chatArrayList.add(chat);

                        }


                        getCallLogs();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Functions.loadingDialog(context, "Loading", false);
                        Functions.showSnackBar(context, error.getMessage());

                    }
                });

    }

    private void getCallLogs() {

        FireRef.CALL_LOGS.child(userDeviceId)
                .child(number.trim())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            CallLogs callLogs = dataSnapshot.getValue(CallLogs.class);
                            Chat chat;
                            assert callLogs != null;
                            chat = new Chat(callLogs.getId(), "", callLogs.getCallDate(),
                                    callLogs.getCallType(), callLogs.getCallDuration(), "", callLogs.getLocation(), callLogs.getUrl(), callLogs.isRead());
                            chatArrayList.add(chat);

                        }

                        getWhatsappMessages();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Functions.loadingDialog(context, "Loading", false);
                        Functions.showSnackBar(context, error.getMessage());
                    }
                });
    }

    private void getWhatsappMessages() {

        FireRef.WhATSAPP_MESSAGE.child(userDeviceId)
                .child(number.trim())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            WhatsAppMessage whatsAppMessage = dataSnapshot.getValue(WhatsAppMessage.class);

                            assert whatsAppMessage != null;
                            Chat chat = new Chat(whatsAppMessage.getId(), whatsAppMessage.getMsg(), whatsAppMessage.getTime()
                                    , whatsAppMessage.getType(), "", "", whatsAppMessage.getLocation(), "no", whatsAppMessage.isRead());
                            chatArrayList.add(chat);

                        }


                        getWhatsappAudioCall();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Functions.loadingDialog(context, "Loading", false);
                        Functions.showSnackBar(context, error.getMessage());
                    }
                });

    }

    private void getWhatsappAudioCall() {

        FireRef.WhATSAPP_A_CALL_LOG.child(userDeviceId)
                .child(number.trim())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            WhatsappCallLogs whatsappCallLogs = dataSnapshot.getValue(WhatsappCallLogs.class);

                            assert whatsappCallLogs != null;
                            Chat chat = new Chat(whatsappCallLogs.getId(), "", whatsappCallLogs.getCallDate(),
                                    whatsappCallLogs.getCallType(), whatsappCallLogs.getCallDuration(), "",
                                    whatsappCallLogs.getLocation(), whatsappCallLogs.getUrl(), whatsappCallLogs.isRead());
                            chatArrayList.add(chat);

                        }


                        getWhatsappVideoCall();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Functions.loadingDialog(context, "Loading", false);
                        Functions.showSnackBar(context, error.getMessage());
                    }
                });

    }

    private void getWhatsappVideoCall() {

        FireRef.WhATSAPP_V_CALL_LOG.child(userDeviceId)
                .child(number.trim())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            WhatsappCallLogs whatsappCallLogs = dataSnapshot.getValue(WhatsappCallLogs.class);

                            assert whatsappCallLogs != null;
                            Chat chat = new Chat(whatsappCallLogs.getId(), "", whatsappCallLogs.getCallDate(),
                                    whatsappCallLogs.getCallType(), whatsappCallLogs.getCallDuration(), "",
                                    whatsappCallLogs.getLocation(), whatsappCallLogs.getUrl(), whatsappCallLogs.isRead());
                            chatArrayList.add(chat);

                        }


                        getPreRecordings();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Functions.loadingDialog(context, "Loading", false);
                        Functions.showSnackBar(context, error.getMessage());
                    }
                });

    }

    private void getPreRecordings() {

        FireRef.PRE_REC.child(userDeviceId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ArrayList<String> arrayList = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            CallLogs callLogs = dataSnapshot.getValue(CallLogs.class);
                            Chat chat;
                            assert callLogs != null;
                            chat = new Chat(callLogs.getId(), "", callLogs.getCallDate(),
                                    callLogs.getCallType(), callLogs.getCallDuration(), "", callLogs.getLocation(), callLogs.getUrl(), callLogs.isRead());

                            chatArrayList.add(chat);

                            arrayList.add(callLogs.getId());
                        }



                        setAdapter(sortBy);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Functions.loadingDialog(context, "Loading", false);
                        Functions.showSnackBar(context, error.getMessage());
                    }
                });

    }

    private void setAdapter(String sortBy) {

        Collections.sort(chatArrayList, (o1, o2) -> o1.getTime().compareTo(o2.getTime()));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false){
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);

                Functions.loadingDialog(context, "Loading", false);

            }
        };

        switch (sortBy) {

            case Constants.ALL: {

                filterdArrayList.clear();
                filterdArrayList.addAll(chatArrayList);
                break;

            }
            case Constants.SIM_CALL: {

                filterdArrayList.clear();
                for (Chat chat : chatArrayList) {

                    if (chat.getType().equals(Constants.OUTGOING_SIM_CALL)
                            || chat.getType().equals(Constants.INCOMING_SIM_CALL)
                            || chat.getType().equals(Constants.OUTGOING_REC_SIM_CALL)
                            || chat.getType().equals(Constants.INCOMING_REC_SIM_CALL)
                            || chat.getType().equals(Constants.MISSED_SIM_CALL)
                            || chat.getType().equals(Constants.REJECT_SIM_CALL)) {

                        filterdArrayList.add(chat);

                    }

                }

                break;

            }

            case Constants.SIM_MESSAGE: {

                filterdArrayList.clear();
                for (Chat chat : chatArrayList) {

                    if (chat.getType().equals(Constants.SENT_SIM_MSG) || chat.getType().equals(Constants.RECEIVE_SIM_MSG)) {

                        filterdArrayList.add(chat);

                    }

                }
                break;

            }

            case Constants.WA_CALL: {

                filterdArrayList.clear();
                for (Chat chat : chatArrayList) {

                    if (chat.getType().equals(Constants.WHATSAPP_INCOMING_AUDIO)
                            || chat.getType().equals(Constants.WHATSAPP_OUTGOING_AUDIO)
                            || chat.getType().equals(Constants.WHATSAPP_MISSED_AUDIO)
                            || chat.getType().equals(Constants.WHATSAPP_INCOMING_VIDEO)
                            || chat.getType().equals(Constants.WHATSAPP_MISSED_VIDEO)
                            || chat.getType().equals(Constants.WHATSAPP_OUTGOING_VIDEO)) {

                        filterdArrayList.add(chat);

                    }

                }
                break;

            }

            case Constants.WA_MESSAGE: {

                filterdArrayList.clear();
                for (Chat chat : chatArrayList) {

                    if (chat.getType().equals(Constants.RECEIVED_WhATSAPP_MESSAGE)) {

                        filterdArrayList.add(chat);

                    }

                }

                break;

            }

            case Constants.PRE_REC: {

                filterdArrayList.clear();
                for (Chat chat : chatArrayList) {

                    if (chat.getType().equals(Constants.PRE_REC)) {

                        filterdArrayList.add(chat);

                    }

                }

                break;

            }

        }

        chatAdapter = new ChatAdapter(context, filterdArrayList);
        rvChat.setLayoutManager(linearLayoutManager);
        rvChat.setAdapter(chatAdapter);

        linearLayoutManager.scrollToPositionWithOffset(filterdArrayList.size() - 1, 0);


        if (filterdArrayList.size() > 0) {

            llNoChat.setVisibility(View.GONE);

        } else {

            llNoChat.setVisibility(View.VISIBLE);

        }

        markAsRead();

    }

    private void markAsRead() {


        for (int i = 0; i < chatArrayList.size(); i++) {

            Chat chat = chatArrayList.get(i);

            String type = chat.getType();

            String pushId = chat.getTime() + number;
            String pushId2 = chat.getId();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(Constants.READ, true);


            if (type != null) {
                switch (type) {

                    case Constants.SENT_SIM_MSG:
                    case Constants.RECEIVE_SIM_MSG: {

                        FireRef.SIM_SMS.child(userDeviceId)
                                .child(number)
                                .child(pushId)
                                .updateChildren(hashMap);
                        break;
                    }
                    case Constants.INCOMING_REC_SIM_CALL:
                    case Constants.OUTGOING_REC_SIM_CALL:
                    case Constants.REJECT_SIM_CALL:
                    case Constants.OUTGOING_SIM_CALL:
                    case Constants.MISSED_SIM_CALL:
                    case Constants.INCOMING_SIM_CALL: {

                        FireRef.CALL_LOGS.child(userDeviceId)
                                .child(number)
                                .child(pushId)
                                .updateChildren(hashMap);

                        break;
                    }

                    case Constants.RECEIVED_WhATSAPP_MESSAGE: {

                        FireRef.WhATSAPP_MESSAGE.child(userDeviceId)
                                .child(number)
                                .child(pushId)
                                .updateChildren(hashMap);

                        break;
                    }

                    case Constants.WHATSAPP_OUTGOING_AUDIO:
                    case Constants.WHATSAPP_MISSED_AUDIO:
                    case Constants.WHATSAPP_INCOMING_AUDIO: {

                        FireRef.WhATSAPP_A_CALL_LOG.child(userDeviceId)
                                .child(number)
                                .child(pushId)
                                .updateChildren(hashMap);

                        break;
                    }
                    case Constants.WHATSAPP_OUTGOING_VIDEO:
                    case Constants.WHATSAPP_MISSED_VIDEO:
                    case Constants.WHATSAPP_INCOMING_VIDEO: {

                        FireRef.WhATSAPP_V_CALL_LOG.child(userDeviceId)
                                .child(number)
                                .child(pushId)
                                .updateChildren(hashMap);

                        break;
                    }

                    case Constants.PRE_REC: {

                        FireRef.PRE_REC.child(userDeviceId).child(pushId2)
                                .updateChildren(hashMap);

                        break;
                    }
                }
            }

        }
    }

    private void setData() {


        fChar = name.substring(0, 1);


        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.round_bg);
        assert unwrappedDrawable != null;
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);

        tvFirstChar.setBackground(wrappedDrawable);

        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(color));

        tvFirstChar.setText(fChar);
        tvName.setText(name);


        getSIMSMS();

    }


    private void clickListeners() {

        layoutBack.setOnClickListener(v -> onBackPressed());

        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showPopup();

            }
        });


        ivLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, LocationHistoryActivity.class);
                intent.putExtra(Constants.CHAT, chatArrayList);
                startActivity(intent);


            }
        });
    }

    private void showPopup() {

        PopupMenu popup = new PopupMenu(context, ivFilter);
        popup.inflate(R.menu.chat_filter_menu);


        switch (sortBy) {

            case Constants.ALL: {

                popup.getMenu().findItem(R.id.menu_by_all).setChecked(true);
                break;

            }

            case Constants.SIM_CALL: {

                popup.getMenu().findItem(R.id.menu_by_sim_call).setChecked(true);
                break;

            }

            case Constants.SIM_MESSAGE: {

                popup.getMenu().findItem(R.id.menu_by_sim_msg).setChecked(true);
                break;

            }

            case Constants.WA_CALL: {

                popup.getMenu().findItem(R.id.menu_by_whatsapp_call).setChecked(true);
                break;

            }

            case Constants.WA_MESSAGE: {

                popup.getMenu().findItem(R.id.menu_by_whatsapp_msg).setChecked(true);
                break;

            }

            case Constants.PRE_REC: {

                popup.getMenu().findItem(R.id.menu_by_call_rec).setChecked(true);
                break;

            }

        }


        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.menu_by_all) {

                popup.getMenu().findItem(R.id.menu_by_all).setChecked(true);
                sortBy = Constants.ALL;

            } else if (item.getItemId() == R.id.menu_by_sim_call) {

                popup.getMenu().findItem(R.id.menu_by_sim_call).setChecked(true);
                sortBy = Constants.SIM_CALL;

            } else if (item.getItemId() == R.id.menu_by_sim_msg) {

                popup.getMenu().findItem(R.id.menu_by_sim_msg).setChecked(true);
                sortBy = Constants.SIM_MESSAGE;

            } else if (item.getItemId() == R.id.menu_by_whatsapp_call) {

                popup.getMenu().findItem(R.id.menu_by_whatsapp_call).setChecked(true);
                sortBy = Constants.WA_CALL;

            } else if (item.getItemId() == R.id.menu_by_whatsapp_msg) {

                popup.getMenu().findItem(R.id.menu_by_whatsapp_msg).setChecked(true);
                sortBy = Constants.WA_MESSAGE;

            } else if (item.getItemId() == R.id.menu_by_call_rec) {

                popup.getMenu().findItem(R.id.menu_by_call_rec).setChecked(true);
                sortBy = Constants.PRE_REC;

            }

            setAdapter(sortBy);

            return true;
        });

        popup.show();
    }


    public void showMusicBtmSheet(Chat chat) {

        mediaPlayer = new MediaPlayer();

        View view1 = getLayoutInflater().inflate(R.layout.layout_music_player_bottom_sheet, null);
        dialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        dialog.setContentView(view1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sbMusic = dialog.findViewById(R.id.seekBar);
        ivPlayPause = dialog.findViewById(R.id.iv_play_pause);
        ivRecType = dialog.findViewById(R.id.iv_rec_type);
        tvMusicTime = dialog.findViewById(R.id.tv_start_time);
        tvTotalTime = dialog.findViewById(R.id.tv_total_time);
        pbMusic = dialog.findViewById(R.id.ProgressBar01);
        ImageView ivClose = dialog.findViewById(R.id.iv_close);
        TextView tvName = dialog.findViewById(R.id.tv_name);

        dialog.show();

        if (!name.equals("")) {

            tvName.setText(name);

        } else {

            tvName.setText(number);

        }


        setMusicType(chat);

        pbMusic.setVisibility(View.VISIBLE);
        ivPlayPause.setVisibility(View.INVISIBLE);

        sbMusic.setEnabled(false);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code

                playMusic(true, chat.getUrl());

            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                if (mediaPlayer != null) {
                    handler.removeCallbacksAndMessages(null);
                    mediaPlayer.stop();
                    mediaPlayer = null;
                    isPlaying = false;

                }
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                mediaPlayer.stop();
                mediaPlayer = null;
                dialog.dismiss();
                isPlaying = false;

            }
        });

        ivPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pbMusic.setVisibility(View.VISIBLE);
                ivPlayPause.setVisibility(View.INVISIBLE);

                if (mediaPlayer.isPlaying()) {

                    ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_white));
                    mediaPlayer.pause();

                    pbMusic.setVisibility(View.INVISIBLE);
                    ivPlayPause.setVisibility(View.VISIBLE);


                } else {

                    mediaPlayer.start();
                    ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_pause_white));
                    pbMusic.setVisibility(View.INVISIBLE);
                    ivPlayPause.setVisibility(View.VISIBLE);
                }

            }
        });


        sbMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void setMusicType(Chat chat) {

        String type = chat.getType();

        switch (type) {

            case Constants.OUTGOING_REC_SIM_CALL:
            case Constants.INCOMING_REC_SIM_CALL: {

                ivRecType.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_voice_call));
                break;
            }

            case Constants.WHATSAPP_INCOMING_AUDIO: {

                ivRecType.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_phone));
                break;
            }
            case Constants.WHATSAPP_OUTGOING_AUDIO: {

                ivRecType.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_videocam));
                break;
            }

            case Constants.PRE_REC: {

                ivRecType.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_voice_call));
                break;
            }


        }

    }

    private void playMusic(boolean b, String url) {

        if (mediaPlayer != null) {
            try {

                mediaPlayer.setDataSource(url);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepareAsync();


            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            mediaPlayer.setOnPreparedListener(mp1 -> new Handler(Looper.getMainLooper()).post(() -> {
                if (mediaPlayer != null) {
                    isPlaying = true;
                    sbMusic.setEnabled(true);

                    totalTime = mediaPlayer.getDuration();
                    mediaPlayer.start();

                    sbMusic.setMax(totalTime);


                    pbMusic.setVisibility(View.INVISIBLE);
                    ivPlayPause.setVisibility(View.VISIBLE);


                    ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_pause_white));

                    total = String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(totalTime), TimeUnit.MILLISECONDS.toSeconds(totalTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalTime)));

                    tvTotalTime.setText(total + "");

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            int duration = mediaPlayer.getCurrentPosition();

                            String runningTime = String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));


                            ///String total = String.valueOf(mediaPlayer.getDuration());
                            String time = runningTime;


                            tvMusicTime.setText(time);

                            sbMusic.setProgress(duration);

                            handler.postDelayed(this, 100);
                        }
                    });
                }

            }));

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    isPlaying = false;
                    sbMusic.setEnabled(true);

                }
            });

        }

    }

    @Override
    public void onBackPressed() {

        if (dialog != null && dialog.isShowing()) {

            handler.removeCallbacksAndMessages(null);
            mediaPlayer.stop();
            mediaPlayer = null;
            dialog.dismiss();
            isPlaying = false;

        } else {

            super.onBackPressed();

        }
    }
}