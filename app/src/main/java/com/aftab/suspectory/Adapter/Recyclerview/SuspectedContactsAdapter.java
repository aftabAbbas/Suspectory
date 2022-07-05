package com.aftab.suspectory.Adapter.Recyclerview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aftab.suspectory.Activities.Main.ChatActivity;
import com.aftab.suspectory.Model.SuspectedContacts;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;
import com.aftab.suspectory.Utills.Functions;
import com.l4digital.fastscroll.FastScroller;

import java.util.ArrayList;

public class SuspectedContactsAdapter extends RecyclerView.Adapter<SuspectedContactsAdapter.VH> implements FastScroller.SectionIndexer {

    Context context;
    ArrayList<SuspectedContacts> contactsList;

    public SuspectedContactsAdapter(Context context, ArrayList<SuspectedContacts> contactsList) {
        this.context = context;
        this.contactsList = contactsList;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.suspected_contacts_item, parent, false);

        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {


        setData(holder, contactsList.get(position));

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(Constants.FROM, Constants.SUSPECTED_CONTACTS);
            intent.putExtra(Constants.CONTACTS, contactsList.get(position));
           /* Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(holder.tvFirstChar, "tv_f_char");
            pairs[1] = new Pair<View, String>(holder.tvName, "tv_name");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

            //getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(2000));
*/
//            context.startActivity(intent, options.toBundle());
            context.startActivity(intent);

        });
    }

    private void setData(VH holder, SuspectedContacts contacts) {

        String name = contacts.getName();
        String number = contacts.getNumber();
        String color = contacts.getColor();
        String fChar = name.substring(0, 1);
        String unReadCount = contacts.getUnReadCount() + "";
        String timeDate = "";

        if (!contacts.getLastNoti().equals("0")) {

            timeDate = Functions.getDateTime(contacts.getLastNoti());

        }
        if (unReadCount.equals("0")) {

            holder.clUnreadCount.setVisibility(View.GONE);

        } else {

            holder.clUnreadCount.setVisibility(View.VISIBLE);

        }

        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.round_bg);
        assert unwrappedDrawable != null;
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);

        holder.tvFirstChar.setBackground(wrappedDrawable);

        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(color));

        holder.tvFirstChar.setText(fChar);
        holder.tvName.setText(name);
        holder.tvPhone.setText(number);
        holder.tvUnReadCount.setText(unReadCount);
        holder.tvLastNoti.setText(timeDate);

    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }


    @Override
    public CharSequence getSectionText(int i) {
        return contactsList.get(i).getName().substring(0, 1);
    }

    public static class VH extends RecyclerView.ViewHolder {

        TextView tvFirstChar, tvName, tvPhone, tvUnReadCount, tvLastNoti;
        ImageView ivAdd;
        ConstraintLayout clUnreadCount;

        public VH(@NonNull View itemView) {
            super(itemView);
            ivAdd = itemView.findViewById(R.id.iv_add);
            tvFirstChar = itemView.findViewById(R.id.tv_f_char);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvUnReadCount = itemView.findViewById(R.id.tv_noti_count);
            tvLastNoti = itemView.findViewById(R.id.tv_last_noti);
            clUnreadCount = itemView.findViewById(R.id.constraintLayout7);
        }
    }
}
