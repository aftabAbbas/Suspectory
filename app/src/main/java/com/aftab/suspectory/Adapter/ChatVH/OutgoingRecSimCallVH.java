package com.aftab.suspectory.Adapter.ChatVH;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftab.suspectory.Activities.Main.ChatActivity;
import com.aftab.suspectory.Activities.Main.ViewLocationActivity;
import com.aftab.suspectory.Model.Chat;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Services.PlayAudioService;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.Functions;


@SuppressLint({"DefaultLocale", "StaticFieldLeak"})
@SuppressWarnings("deprecation")
public class OutgoingRecSimCallVH extends RecyclerView.ViewHolder {
    MediaPlayer mediaPlayer;
    ImageView ivPlayPause;
    ProgressBar progressBar;
    TextView tvRecDuration;
    SeekBar sbRec;
    TextView tvCallTime;
    ImageView ivLocation;
    Handler handler = new Handler();
    PlayAudioService playAudioService;
    Intent mServiceIntent;
    int totalTime;
    boolean isPause, isPlaying;
    String total;

    public OutgoingRecSimCallVH(@NonNull View itemView) {
        super(itemView);
        tvCallTime = itemView.findViewById(R.id.tv_call_time);
        ivLocation = itemView.findViewById(R.id.iv_location1);
        ivPlayPause = itemView.findViewById(R.id.iv_play_pause);
        sbRec = itemView.findViewById(R.id.seekBar);
        progressBar = itemView.findViewById(R.id.ProgressBar01);
        tvRecDuration = itemView.findViewById(R.id.tv_rec_duration);
    }

    public void setData(Chat chat, Context context) {

        String timeDate = Functions.getDateTime(chat.getTime());
        playAudioService = new PlayAudioService();
        mServiceIntent = new Intent(context, PlayAudioService.class);

        tvCallTime.setText(timeDate);


        ivLocation.setOnClickListener(v -> {

            String location = chat.getLocation();
            if (location.equals("No Location") || location.equals("0.0,0.0")) {

                Toast.makeText(context, "Location Not Found!", Toast.LENGTH_SHORT).show();

            } else {

                Intent intent = new Intent(context, ViewLocationActivity.class);
                intent.putExtra(Constants.CHAT, chat);
                context.startActivity(intent);

            }

        });


        ivPlayPause.setOnClickListener(v -> ((ChatActivity) context).showMusicBtmSheet(chat));

    }

}

