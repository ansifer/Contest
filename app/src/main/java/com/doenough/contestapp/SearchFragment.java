package com.doenough.contestapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.doenough.contestapp.event.Event;
import com.doenough.contestapp.search.SearchAdapter;
import com.doenough.contestapp.search.SearchItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<SearchItem> list;
    private SearchAdapter adapter;
    private DatabaseReference reference;
    private TextView emptyView;

    private RecyclerView.LayoutManager layoutManager;

    private ProgressBar progressBar;

    private String name;
    private String eventCover;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        EditText searchEditText = (EditText) getActivity().findViewById(R.id.search_edit_text);
        recyclerView = getActivity().findViewById(R.id.search_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        progressBar = (ProgressBar) getActivity().findViewById(R.id.search_progressbar);
        emptyView = (TextView) getActivity().findViewById(R.id.search_empty_listview);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("contest");
        list = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        searchEvent("");
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    searchEvent(charSequence);
                } else if (charSequence.length() == 0) {
                    searchEvent("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        super.onActivityCreated(savedInstanceState);
    }

    private void searchEvent(final CharSequence charSequence) {
        if (list != null) {
            list.clear();
        }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    emptyView.setVisibility(View.GONE);
                }

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        if(postSnapshot.exists()) {
                            name = postSnapshot.child("name").getValue(String.class).toLowerCase();
                            eventCover = postSnapshot.child("imageurl").getValue(String.class);
                            if (name.contains(charSequence)) {
                                if (eventCover == null) {
                                    list.add(new SearchItem(postSnapshot.getKey(),
                                            postSnapshot.child("name").getValue(String.class),
                                            postSnapshot.child("createdat").getValue(long.class),
                                            postSnapshot.child("nameofcreator").getValue(String.class)));
                                } else {
                                    list.add(new SearchItem(postSnapshot.getKey(),
                                            postSnapshot.child("name").getValue(String.class),
                                            postSnapshot.child("createdat").getValue(long.class),
                                            Uri.parse(eventCover),
                                            postSnapshot.child("nameofcreator").getValue(String.class)));
                                }
                            }
                        }
                    }

                    if (getActivity() != null) {
                        progressBar.setVisibility(View.GONE);
                        //recyclerView.setEmptyView(emptyView);
                        adapter = new SearchAdapter(getActivity(), list);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
