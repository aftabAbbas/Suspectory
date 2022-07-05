package com.aftab.suspectory.Adapter.Recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftab.suspectory.Adapter.ChatVH.IncomingRecSimCallVH;
import com.aftab.suspectory.Adapter.ChatVH.IncomingRecWACallVH;
import com.aftab.suspectory.Adapter.ChatVH.IncomingRecWVCallVH;
import com.aftab.suspectory.Adapter.ChatVH.IncomingSimCallVH;
import com.aftab.suspectory.Adapter.ChatVH.MissedSimCallVH;
import com.aftab.suspectory.Adapter.ChatVH.MissedWACallVH;
import com.aftab.suspectory.Adapter.ChatVH.MissedWVCallVH;
import com.aftab.suspectory.Adapter.ChatVH.OutgoingRecSimCallVH;
import com.aftab.suspectory.Adapter.ChatVH.OutgoingRecWACallVH;
import com.aftab.suspectory.Adapter.ChatVH.OutgoingRecWVCallVH;
import com.aftab.suspectory.Adapter.ChatVH.OutgoingSimCallVH;
import com.aftab.suspectory.Adapter.ChatVH.PreRecCallVH;
import com.aftab.suspectory.Adapter.ChatVH.ReceiveSimMsgVH;
import com.aftab.suspectory.Adapter.ChatVH.ReceiveWhatsAppMsgVH;
import com.aftab.suspectory.Adapter.ChatVH.RejectedSimCallVH;
import com.aftab.suspectory.Adapter.ChatVH.SentSimMsgVH;
import com.aftab.suspectory.Model.Chat;
import com.aftab.suspectory.R;
import com.aftab.suspectory.Utills.Constants;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class ChatAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Chat> simSMSList;

    public ChatAdapter(Context context, ArrayList<Chat> simSMSList) {
        this.context = context;
        this.simSMSList = simSMSList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        String type = getType(viewType);

        View view;


        switch (type) {

            case Constants.SENT_SIM_MSG: {

                view = LayoutInflater.from(context).inflate(R.layout.sent_sim_msg_layout, parent, false);
                return new SentSimMsgVH(view);

            }

            case Constants.RECEIVE_SIM_MSG: {

                view = LayoutInflater.from(context).inflate(R.layout.receive_sim_msg_layout, parent, false);
                return new ReceiveSimMsgVH(view);

            }

            case Constants.OUTGOING_SIM_CALL: {

                view = LayoutInflater.from(context).inflate(R.layout.outgoing_sim_call_layout, parent, false);
                return new OutgoingSimCallVH(view);
            }

            case Constants.OUTGOING_REC_SIM_CALL: {

                view = LayoutInflater.from(context).inflate(R.layout.outgoing_rec_sim_call_layout, parent, false);
                return new OutgoingRecSimCallVH(view);
            }

            case Constants.INCOMING_SIM_CALL: {

                view = LayoutInflater.from(context).inflate(R.layout.incoming_sim_call_layout, parent, false);
                return new IncomingSimCallVH(view);
            }

            case Constants.INCOMING_REC_SIM_CALL: {

                view = LayoutInflater.from(context).inflate(R.layout.incoming_rec_sim_call_layout, parent, false);
                return new IncomingRecSimCallVH(view);
            }

            case Constants.REJECT_SIM_CALL: {

                view = LayoutInflater.from(context).inflate(R.layout.reject_sim_call_layout, parent, false);
                return new RejectedSimCallVH(view);
            }

            case Constants.MISSED_SIM_CALL: {

                view = LayoutInflater.from(context).inflate(R.layout.missed_sim_call_layout, parent, false);
                return new MissedSimCallVH(view);
            }

            case Constants.RECEIVED_WhATSAPP_MESSAGE: {

                view = LayoutInflater.from(context).inflate(R.layout.receive_whatsapp_msg_layout, parent, false);
                return new ReceiveWhatsAppMsgVH(view);
            }

            case Constants.WHATSAPP_INCOMING_AUDIO: {

                view = LayoutInflater.from(context).inflate(R.layout.incoming_rec_w_a_call_layout, parent, false);
                return new IncomingRecWACallVH(view);
            }

            case Constants.WHATSAPP_OUTGOING_AUDIO: {

                view = LayoutInflater.from(context).inflate(R.layout.outgoing_rec_w_a_call_layout, parent, false);
                return new OutgoingRecWACallVH(view);
            }

            case Constants.WHATSAPP_MISSED_AUDIO: {

                view = LayoutInflater.from(context).inflate(R.layout.missed_w_a_call_layout, parent, false);
                return new MissedWACallVH(view);
            }

            case Constants.WHATSAPP_INCOMING_VIDEO: {

                view = LayoutInflater.from(context).inflate(R.layout.incoming_rec_w_v_call_layout, parent, false);
                return new IncomingRecWVCallVH(view);
            }

            case Constants.WHATSAPP_OUTGOING_VIDEO: {

                view = LayoutInflater.from(context).inflate(R.layout.outgoing_rec_w_v_call_layout, parent, false);
                return new OutgoingRecWVCallVH(view);
            }

            case Constants.WHATSAPP_MISSED_VIDEO: {

                view = LayoutInflater.from(context).inflate(R.layout.missed_w_v_call_layout, parent, false);
                return new MissedWVCallVH(view);
            }

            case Constants.PRE_REC: {

                view = LayoutInflater.from(context).inflate(R.layout.pre_a_call_layout, parent, false);
                return new PreRecCallVH(view);
            }

            default:
                view = LayoutInflater.from(context).inflate(R.layout.empty_layout, parent, false);
                return new SentSimMsgVH(view);

        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Chat chat = simSMSList.get(position);

        String type = chat.getType();


        switch (type) {

            case Constants.SENT_SIM_MSG: {

                ((SentSimMsgVH) holder).setData(chat, context);
                break;

            }

            case Constants.RECEIVE_SIM_MSG: {

                ((ReceiveSimMsgVH) holder).setData(chat, context);
                break;
            }

            case Constants.OUTGOING_SIM_CALL: {

                ((OutgoingSimCallVH) holder).setData(chat, context);
                break;
            }

            case Constants.OUTGOING_REC_SIM_CALL: {

                ((OutgoingRecSimCallVH) holder).setData(chat, context);
                break;
            }

            case Constants.INCOMING_SIM_CALL: {

                ((IncomingSimCallVH) holder).setData(chat, context);
                break;
            }

            case Constants.INCOMING_REC_SIM_CALL: {

                ((IncomingRecSimCallVH) holder).setData(chat, context);
                break;
            }

            case Constants.REJECT_SIM_CALL: {

                ((RejectedSimCallVH) holder).setData(chat, context);
                break;
            }

            case Constants.MISSED_SIM_CALL: {

                ((MissedSimCallVH) holder).setData(chat, context);
                break;
            }


            case Constants.RECEIVED_WhATSAPP_MESSAGE: {

                ((ReceiveWhatsAppMsgVH) holder).setData(chat, context);
                break;
            }

            case Constants.WHATSAPP_INCOMING_AUDIO: {

                ((IncomingRecWACallVH) holder).setData(chat, context);
                break;
            }

            case Constants.WHATSAPP_OUTGOING_AUDIO: {

                ((OutgoingRecWACallVH) holder).setData(chat, context);
                break;
            }

            case Constants.WHATSAPP_MISSED_AUDIO: {

                ((MissedWACallVH) holder).setData(chat, context);
                break;
            }

            case Constants.WHATSAPP_INCOMING_VIDEO: {

                ((IncomingRecWVCallVH) holder).setData(chat, context);
                break;
            }

            case Constants.WHATSAPP_OUTGOING_VIDEO: {

                ((OutgoingRecWVCallVH) holder).setData(chat, context);
                break;
            }

            case Constants.WHATSAPP_MISSED_VIDEO: {

                ((MissedWVCallVH) holder).setData(chat, context);
                break;
            }
            case Constants.PRE_REC: {

                ((PreRecCallVH) holder).setData(chat, context);
                break;
            }

        }

    }

    @Override
    public int getItemCount() {
        return simSMSList.size();
    }

    private String getType(int viewType) {

        switch (viewType) {

            case 0: {
                return Constants.SENT_SIM_MSG;
            }

            case 1: {
                return Constants.RECEIVE_SIM_MSG;
            }

            case 2: {
                return Constants.OUTGOING_SIM_CALL;
            }

            case 3: {
                return Constants.OUTGOING_REC_SIM_CALL;
            }

            case 4: {
                return Constants.INCOMING_SIM_CALL;
            }

            case 5: {
                return Constants.INCOMING_REC_SIM_CALL;
            }

            case 6: {
                return Constants.REJECT_SIM_CALL;
            }

            case 7: {
                return Constants.MISSED_SIM_CALL;
            }

            case 8: {
                return Constants.RECEIVED_WhATSAPP_MESSAGE;
            }

            case 9: {
                return Constants.WHATSAPP_INCOMING_AUDIO;
            }

            case 10: {
                return Constants.WHATSAPP_OUTGOING_AUDIO;
            }

            case 11: {
                return Constants.WHATSAPP_MISSED_AUDIO;
            }

            case 12: {
                return Constants.WHATSAPP_INCOMING_VIDEO;
            }

            case 13: {
                return Constants.WHATSAPP_OUTGOING_VIDEO;
            }

            case 14: {
                return Constants.WHATSAPP_MISSED_VIDEO;
            }
            case 15: {
                return Constants.PRE_REC;
            }

            default:
                return "-1";
        }

    }

    @Override
    public int getItemViewType(int position) {

        switch (simSMSList.get(position).getType()) {

            case Constants.SENT_SIM_MSG: {
                return 0;
            }

            case Constants.RECEIVE_SIM_MSG: {
                return 1;
            }

            case Constants.OUTGOING_SIM_CALL: {
                return 2;
            }

            case Constants.OUTGOING_REC_SIM_CALL: {
                return 3;
            }

            case Constants.INCOMING_SIM_CALL: {
                return 4;
            }

            case Constants.INCOMING_REC_SIM_CALL: {
                return 5;
            }

            case Constants.REJECT_SIM_CALL: {
                return 6;
            }

            case Constants.MISSED_SIM_CALL: {
                return 7;
            }

            case Constants.RECEIVED_WhATSAPP_MESSAGE: {
                return 8;
            }

            case Constants.WHATSAPP_INCOMING_AUDIO: {
                return 9;
            }

            case Constants.WHATSAPP_OUTGOING_AUDIO: {
                return 10;
            }

            case Constants.WHATSAPP_MISSED_AUDIO: {
                return 11;
            }

            case Constants.WHATSAPP_INCOMING_VIDEO: {
                return 12;
            }

            case Constants.WHATSAPP_OUTGOING_VIDEO: {
                return 13;
            }

            case Constants.WHATSAPP_MISSED_VIDEO: {
                return 14;
            }

            case Constants.PRE_REC: {
                return 15;
            }
            default:
                return -1;
        }
    }
}
