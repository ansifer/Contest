package com.doenough.contestapp.account.myvote;

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

public class MyVoteAdapter extends RecyclerView.Adapter<MyVoteHolder> {

    private final List<MyVote> mMyVoteList;
    private Context mContext;

    public MyVoteAdapter(Context context, List<MyVote> myVoteList) {
        mMyVoteList = myVoteList;
        mContext = context;
    }


    @Override
    public MyVoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_vote_list_item_view, parent, false);
        return new MyVoteHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(MyVoteHolder holder, int position) {
        MyVote myVote = mMyVoteList.get(position);
        holder.bindEvent(myVote);
    }

    @Override
    public int getItemCount() {
        return mMyVoteList.size();
    }
}
