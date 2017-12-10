package com.doenough.contestapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.doenough.contestapp.event.Event;
import com.doenough.contestapp.event.EventAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class EventFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference eventRef;
    private RecyclerView recyclerView;
    private ArrayList<Event> arrayList;
    private String eventid;
    private String eventName;
    private String eventCover;
    private long eventTime;
    private String creatorName;
    private String creatorUid;

    private RecyclerView.LayoutManager llm;
    private EventAdapter eventAdapter;
    private ProgressBar progressBar;
    private TextView emptyView;

    private int resultcode;

    public EventFragment() {
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
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if(user == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
        recyclerView = (RecyclerView)  getActivity().findViewById(R.id.listings_view);
        progressBar = getActivity().findViewById(R.id.event_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        emptyView = getActivity().findViewById(R.id.event_fragment_empty_view);
        llm = new LinearLayoutManager(getActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("users").child(user.getUid()).child("myevents");
        arrayList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() == 0) {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
                else {
                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    eventid = snapshot.child("eventid").getValue(String.class);
                    eventRef = firebaseDatabase.getReference("contest").child(eventid);
                    eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            eventName = dataSnapshot.child("name").getValue(String.class);
                            eventTime = dataSnapshot.child("createdat").getValue(long.class);
                            eventCover = dataSnapshot.child("imageurl").getValue(String.class);
                            creatorName = dataSnapshot.child("nameofcreator").getValue(String.class);
                            creatorUid = dataSnapshot.child("uidofcreator").getValue(String.class);
                            if(eventCover == null) {
                                arrayList.add(new Event(eventName, eventTime, dataSnapshot.getKey(), snapshot.getKey(), creatorName, creatorUid));
                            }
                            else {
                                arrayList.add(new Event(eventName, eventTime, dataSnapshot.getKey(), snapshot.getKey(), Uri.parse(eventCover), creatorName, creatorUid));
                            }
                            if(getActivity() != null) {
                                eventAdapter = new EventAdapter(getActivity(), arrayList);
                                recyclerView.setLayoutManager(llm);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(eventAdapter);
                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressBar.setVisibility(View.GONE);
                        }

                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        reference.keepSynced(true);
        LinearLayout linearLayout = getActivity().findViewById(R.id.new_contest_button);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getActivity(),NewContestActivty.class));

            }
        });

        super.onActivityCreated(savedInstanceState);
    }

}
