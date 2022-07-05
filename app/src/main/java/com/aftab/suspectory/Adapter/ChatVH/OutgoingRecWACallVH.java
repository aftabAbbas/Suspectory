package com.aftab.suspectory.Adapter.ChatVH;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aftab.suspectory.Activities.Main.ViewLocationActivity;
import com.aftab.suspectory.Model.Chat;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Services.PlayAudioService;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.Functions;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


@SuppressLint({"DefaultLocale", "StaticFieldLeak"})
@SuppressWarnings("deprecation")
public class OutgoingRecWACallVH extends RecyclerView.ViewHolder {
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

    public OutgoingRecWACallVH(@NonNull View itemView) {
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

        String url = chat.getUrl();


        ivPlayPause.setOnClickListener(v -> {

            /*if (!Functions.isMyServiceRunning(playAudioService.getClass(), context)) {

                ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_pause));
                progressBar.setVisibility(View.VISIBLE);
                ivPlayPause.setVisibility(View.INVISIBLE);
                context.startService(new Intent(context, PlayAudioService.class).putExtra(Constants.URL, chat.getUrl()));

            } else {

                progressBar.setVisibility(View.GONE);
                ivPlayPause.setVisibility(View.VISIBLE);
                ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow));
                context.stopService(mServiceIntent);

            }*/


            /*mediaPlayer.setOnCompletionListener(mp -> {

                ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow));
                mediaPlayer.release();
                mediaPlayer.stop();


            });


            if (mediaPlayer.isPlaying()) {

                ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow));
                mediaPlayer.stop();

            } else {

                mediaPlayer.start();
                ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_pause));
                progressBar.setVisibility(View.INVISIBLE);
            }
*/

            ivPlayPause.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            if (mediaPlayer != null && isPlaying && !isPause) {

                ivPlayPause.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_white));
                mediaPlayer.pause();
                isPause = true;
                isPlaying = false;

            } else if (isPause && !isPlaying) {

                isPause = false;
                isPlaying = true;

                ivPlayPause.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_pause_white));

                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {

                        mediaPlayer.pause();

                    }
                }

                mediaPlayer.start();

                String total = String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(totalTime), TimeUnit.MILLISECONDS.toSeconds(totalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalTime)));

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        int duration = mediaPlayer.getCurrentPosition();

                        String runningTime = String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));


                        ///String total = String.valueOf(mediaPlayer.getDuration());
                        String time = runningTime + "/" + total;


                        tvRecDuration.setText(time);

                        sbRec.setProgress(duration);


                        handler.postDelayed(this, 100);
                    }
                });


                mediaPlayer.setOnCompletionListener(mp -> {

                    ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_white));

                    String runningTime = String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(0), TimeUnit.MILLISECONDS.toSeconds(0) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(0)));

                    String time = runningTime + "/" + total;

                    tvRecDuration.setText(time);

                    mediaPlayer.stop();
                    sbRec.setProgress(0);
                    mediaPlayer = null;
                    handler.removeCallbacksAndMessages(null);


                });

            } else if (!isPlaying && !isPause) {

                isPlaying = true;
                isPause = false;

                ivPlayPause.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_pause_white));

                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {

                        mediaPlayer.pause();

                    }
                }
                mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();

                    mediaPlayer.start();

                    totalTime = mediaPlayer.getDuration();

                    sbRec.setMax(totalTime);


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                mediaPlayer.setOnPreparedListener(mp1 -> new Handler(Looper.getMainLooper()).post(() -> {

                    progressBar.setVisibility(View.INVISIBLE);
                    ivPlayPause.setVisibility(View.VISIBLE);

                    total = String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(totalTime), TimeUnit.MILLISECONDS.toSeconds(totalTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalTime)));


                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            int duration = mediaPlayer.getCurrentPosition();

                            String runningTime = String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));


                            ///String total = String.valueOf(mediaPlayer.getDuration());
                            String time = runningTime + "/" + total;


                            tvRecDuration.setText(time);

                            sbRec.setProgress(duration);

                            handler.postDelayed(this, 100);
                        }
                    });


                }));


                mediaPlayer.setOnCompletionListener(mp -> {

                    ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_white));

                    String runningTime = String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(0), TimeUnit.MILLISECONDS.toSeconds(0) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(0)));


                    ///String total = String.valueOf(mediaPlayer.getDuration());
                    String time = runningTime + "/" + total;


                    tvRecDuration.setText(time);

                    //  mediaPlayer.release();
                    mediaPlayer.stop();
                    sbRec.setProgress(0);
                    mediaPlayer = null;
                    handler.removeCallbacksAndMessages(null);


                });
            }


            /*if (mediaPlayer != null && mediaPlayer.isPlaying()) {

                ivPlayPause.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow));
                mediaPlayer.pause();
                isPause = true;
                isPlaying = false;

            }

            else if (isPause) {

                isPause = false;
                isPlaying = true;

                mediaPlayer.setOnPreparedListener(mp1 -> new Handler(Looper.getMainLooper()).post(() -> {

                    progressBar.setVisibility(View.GONE);
                    ivPlayPause.setVisibility(View.VISIBLE);

                    String total = String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(totalTime), TimeUnit.MILLISECONDS.toSeconds(totalTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalTime)));


                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            int duration = mediaPlayer.getCurrentPosition();

                            String runningTime = String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));


                            ///String total = String.valueOf(mediaPlayer.getDuration());
                            String time = runningTime + "/" + total;


                            tvRecDuration.setText(time);

                            sbRec.setProgress(duration);

                            handler.postDelayed(this, 100);
                        }
                    });


                }));

                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(mp -> {

                    ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow));
                    //  mediaPlayer.release();
                    mediaPlayer.stop();
                    sbRec.setProgress(0);


                });

            } else {

                isPlaying = true;
                ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_pause));
                ivPlayPause.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);

                mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();

                    mediaPlayer.start();

                    totalTime = mediaPlayer.getDuration();

                    sbRec.setMax(totalTime);


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                mediaPlayer.setOnPreparedListener(mp1 -> new Handler(Looper.getMainLooper()).post(() -> {

                    progressBar.setVisibility(View.GONE);
                    ivPlayPause.setVisibility(View.VISIBLE);

                    String total = String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(totalTime), TimeUnit.MILLISECONDS.toSeconds(totalTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalTime)));


                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            int duration = mediaPlayer.getCurrentPosition();

                            String runningTime = String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));


                            ///String total = String.valueOf(mediaPlayer.getDuration());
                            String time = runningTime + "/" + total;


                            tvRecDuration.setText(time);

                            sbRec.setProgress(duration);

                            handler.postDelayed(this, 100);
                        }
                    });


                }));


                mediaPlayer.setOnCompletionListener(mp -> {

                    ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow));
                    //  mediaPlayer.release();
                    mediaPlayer.stop();
                    sbRec.setProgress(0);


                });
                ///  progressBar.setVisibility(View.INVISIBLE);
            }*/


        });


    }

}

