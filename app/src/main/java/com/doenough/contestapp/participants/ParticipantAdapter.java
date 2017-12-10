package com.doenough.contestapp.participants;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doenough.contestapp.R;
import com.doenough.contestapp.event.Event;
import com.doenough.contestapp.event.EventHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanchit on 18/11/17.
 */

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantHolder> {

    private Context mContext;
    private List<Participant> mParticipants;
    private String mEventId;

    public ParticipantAdapter(String eventid, Context context, List<Participant> participants) {
        mContext = context;
        mParticipants = participants;
        mEventId = eventid;
    }
    @Override
    public ParticipantHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant_list_item_view, parent, false);
        return new ParticipantHolder(mEventId, mContext, view);
    }

    @Override
    public void onBindViewHolder(ParticipantHolder holder, int position) {
        Participant participant = mParticipants.get(position);
        holder.bindParticipant(participant);
    }

    @Override
    public int getItemCount() {
        return mParticipants.size();
    }
}
