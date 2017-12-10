package com.doenough.contestapp.event;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.doenough.contestapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by sanchit on 18/11/17.
 */

public class EventAdapter extends RecyclerView.Adapter<EventHolder> {

    private final List<Event> mEventList;
    private Context mContext;

    public EventAdapter(Context context, List<Event> eventList) {
        mEventList = eventList;
        mContext = context;
    }


    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view, parent, false);
        return new EventHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        Event event = mEventList.get(position);
        holder.bindEvent(event);
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }
}
