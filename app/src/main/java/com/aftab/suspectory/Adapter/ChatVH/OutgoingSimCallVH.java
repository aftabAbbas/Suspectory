package com.aftab.suspectory.Adapter.ChatVH;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftab.suspectory.Activities.Main.ViewLocationActivity;
import com.aftab.suspectory.Model.Chat;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.Functions;

@SuppressLint("DefaultLocale")
public class OutgoingSimCallVH extends RecyclerView.ViewHolder {
    private final TextView tvCallDetail, tvCallDuration;
    private final ImageView ivLocation;


    public OutgoingSimCallVH(@NonNull View itemView) {
        super(itemView);
        tvCallDetail = itemView.findViewById(R.id.tv_call_detail);
        tvCallDuration = itemView.findViewById(R.id.tv_call_duration);
        ivLocation = itemView.findViewById(R.id.iv_location);
    }

    public void setData(Chat chat, Context context) {

        String timeDate = Functions.getDateTime(chat.getTime());


        String detail = "Call At " + timeDate;
        String duration = chat.getDuration();

        int totalSecs = Integer.parseInt(duration);

        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        tvCallDetail.setText(detail);
        tvCallDuration.setText(timeString);

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
    }
           /* MediaPlayer mediaPlayer = new MediaPlayer();
            clRecordedCall.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            clSimpleCall.setVisibility(View.GONE);

            tvCallTime.setText(timeDate);

            try {


                ivPlayPause.setVisibility(View.INVISIBLE);
                ivPlayPause.setClickable(false);
                mediaPlayer.setDataSource(chat.getUrl());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(mp1 -> {

                    progressBar.setVisibility(View.GONE);
                    ivPlayPause.setVisibility(View.VISIBLE);
                    ivPlayPause.setClickable(true);

                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            ivPlayPause.setOnClickListener(v -> {

                if (mediaPlayer.isPlaying()) {

                    mediaPlayer.pause();

                } else {

                    mediaPlayer.start();
                }

            });
*/
}


