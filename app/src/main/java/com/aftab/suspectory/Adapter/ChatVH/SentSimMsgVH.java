package com.aftab.suspectory.Adapter.ChatVH;

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

public class SentSimMsgVH extends RecyclerView.ViewHolder {
    private final TextView tvMessage;
    private final TextView tvDate;
    private final ImageView ivLocation;

    public SentSimMsgVH(@NonNull View itemView) {
        super(itemView);
        tvMessage = itemView.findViewById(R.id.tv_sent_message);
        tvDate = itemView.findViewById(R.id.tv_sent_date);
        ivLocation = itemView.findViewById(R.id.iv_location);
    }

    public void setData(Chat chat, Context context) {

        String message = chat.getMessage().trim();
        String timeDate = Functions.getDateTime(chat.getTime());

        tvMessage.setText(message);
        tvDate.setText(timeDate);

        ivLocation.setOnClickListener(v -> {

            String location = chat.getLocation();
            if (location.equals("No Location")|| location.equals("0.0,0.0")) {

                Toast.makeText(context, "Location Not Found!", Toast.LENGTH_SHORT).show();

            } else {

                Intent intent = new Intent(context, ViewLocationActivity.class);
                intent.putExtra(Constants.CHAT, chat);
                context.startActivity(intent);

            }

        });
    }
}
