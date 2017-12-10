package com.doenough.contestapp.search;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class SearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context mContext;
    private TextView nameTextView;
    private TextView timeTextView;
    private ImageView imageView;
    private TextView creatorNameTextView;
    private SearchItem mSearchItem;


    public SearchHolder(Context context, View itemView) {
        super(itemView);

        mContext = context;
        nameTextView = (TextView) itemView.findViewById(R.id.search_name);
        timeTextView = (TextView) itemView.findViewById(R.id.search_time);
        imageView = itemView.findViewById(R.id.search_event_cover);
        creatorNameTextView = itemView.findViewById(R.id.name_of_creator_list_item_view);
        itemView.setOnClickListener(this);
    }

    public void bindSearchItem(SearchItem searchItem) {
        mSearchItem = searchItem;
        if (searchItem.getmImageUrl() != null)
            Glide.with(mContext).load(mSearchItem.getmImageUrl()).into(imageView);
        nameTextView.setText(searchItem.getmName());
        timeTextView.setText(getDate(searchItem.getmTime()));
        creatorNameTextView.setText(searchItem.getmCreatorName());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, VoteAnyoneActivity.class);
        intent.putExtra("eventid", mSearchItem.getmKey());
        intent.putExtra("eventname", mSearchItem.getmName());
        intent.putExtra("eventtime", mSearchItem.getmTime());
        intent.putExtra("eventcover", mSearchItem.getmImageUrl());
        mContext.startActivity(intent);
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
