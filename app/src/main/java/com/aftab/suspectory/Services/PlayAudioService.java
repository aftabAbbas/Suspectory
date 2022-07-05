package com.aftab.suspectory.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.aftab.suspectory.Adapter.ChatVH.OutgoingRecSimCallVH;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
@SuppressLint("DefaultLocale")
public class PlayAudioService extends Service {

    private final Handler handler = new Handler();
    MediaPlayer mediaPlayer;
    Context context;
    int totalTime;

    public PlayAudioService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            startMyOwnForeground();


        } else {

            startForeground(1, new Notification());


        }


      /*  mediaPlayer = new MediaPlayer();

        String url = intent.getStringExtra(Constants.URL);

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.start();

            totalTime = mediaPlayer.getDuration();

            OutgoingRecSimCallVH.sbRec.setMax(totalTime);


        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        mediaPlayer.setOnPreparedListener(mp1 -> new Handler(Looper.getMainLooper()).post(() -> {

            OutgoingRecSimCallVH.progressBar.setVisibility(View.GONE);
            OutgoingRecSimCallVH.ivPlayPause.setVisibility(View.VISIBLE);

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


                    OutgoingRecSimCallVH.tvRecDuration.setText(time);

                    OutgoingRecSimCallVH.sbRec.setProgress(duration);

                    handler.postDelayed(this, 100);
                }
            });


        }));


        mediaPlayer.setOnCompletionListener(mp -> {

            OutgoingRecSimCallVH.ivPlayPause.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow));
            mediaPlayer.release();
            mediaPlayer.stop();


        });
*/

        return START_STICKY;
    }

    private void startMyOwnForeground() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String NOTIFICATION_CHANNEL_ID = "com.aftab.suspectory";
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Playing Audio")
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .build();
            startForeground(2, notification);


        }
    }


}