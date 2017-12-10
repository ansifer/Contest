package com.doenough.contestapp.account.myevent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doenough.contestapp.R;

import java.util.List;

/**
 * Created by sanchit on 18/11/17.
 */

public class MyEventAdapter extends RecyclerView.Adapter<MyEventHolder> {

    private final List<MyEvent> mMyEventList;
    private Context mContext;

    public MyEventAdapter(Context context, List<MyEvent> myEventList) {
        mMyEventList = myEventList;
        mContext = context;
    }


    @Override
    public MyEventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_event_list_item_view, parent, false);
        return new MyEventHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(MyEventHolder holder, int position) {
        MyEvent myEvent = mMyEventList.get(position);
        holder.bindEvent(myEvent);
    }

    @Override
    public int getItemCount() {
        return mMyEventList.size();
    }
}
