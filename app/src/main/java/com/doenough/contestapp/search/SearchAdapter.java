package com.doenough.contestapp.search;

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
import com.doenough.contestapp.event.Event;
import com.doenough.contestapp.event.EventHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanchit on 19/11/17.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchHolder> {

    private List<SearchItem> mSearchItems;
    private Context mContext;

    public SearchAdapter(Context context, List<SearchItem> searchItems) {
        mContext = context;
        mSearchItems = searchItems;
    }


    @Override
    public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
        return new SearchHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(SearchHolder holder, int position) {
        SearchItem searchItem = mSearchItems.get(position);
        holder.bindSearchItem(searchItem);
    }

    @Override
    public int getItemCount() {
        return mSearchItems.size();
    }
}
