package com.doenough.contestapp.account.myvote;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doenough.contestapp.R;
import com.doenough.contestapp.VoteAnyoneActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sanchit on 22/11/17.
 */

public class MyVoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView nameTextView;
    private TextView timeTextView;
    private ImageView imageView;
    private TextView creatorNameTextView;
    private Context mContext;
    private MyVote mMyVote;

    public MyVoteHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        nameTextView = itemView.findViewById(R.id.event_name_list_text_view);
        timeTextView =  itemView.findViewById(R.id.event_time_list_text_view);
        creatorNameTextView = itemView.findViewById(R.id.name_of_creator_list_item_view);
        imageView = itemView.findViewById(R.id.event_cover);
        itemView.setOnClickListener(this);
    }

    public void bindEvent(MyVote myVote) {
        mMyVote = myVote;
        if(myVote.getmImageUrl() != null)
        Glide.with(mContext).load(mMyVote.getmImageUrl()).into(imageView);
        nameTextView.setText(mMyVote.getmName());
        timeTextView.setText(getDate(mMyVote.getmTime()));
        creatorNameTextView.setText(mMyVote.getmNameOfCreator());
    }

    @Override
    public void onClick(View v) {
        if (mMyVote != null) {
            Intent intent = new Intent(mContext,VoteAnyoneActivity.class);
            intent.putExtra("eventid", mMyVote.getmKey());
            intent.putExtra("eventname", mMyVote.getmName());
            intent.putExtra("eventtime", mMyVote.getmTime());
            intent.putExtra("eventcover", mMyVote.getmImageUrl());
            intent.putExtra("userkey", mMyVote.getmUserKey());
            mContext.startActivity(intent);
        }
    }

    private String getDate(long timeStamp){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH);
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }
}
