package com.doenough.contestapp.account_fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.doenough.contestapp.LoginActivity;
import com.doenough.contestapp.R;
import com.doenough.contestapp.account.myevent.MyEvent;
import com.doenough.contestapp.account.myevent.MyEventAdapter;
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


public class MyEventFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference eventRef;
    private RecyclerView recyclerView;
    private ArrayList<MyEvent> arrayList;
    private String eventid;
    private String eventName;
    private String eventCover;
    private long eventTime;
    private String creatorName;
    private String creatorUid;
    private MyEventAdapter eventAdapter;


    public MyEventFragment() {
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
        return inflater.inflate(R.layout.fragment_my_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if(user == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
        recyclerView = (RecyclerView)  getActivity().findViewById(R.id.account_my_events);


        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("users").child(user.getUid()).child("myevents");
        arrayList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /*if(eventAdapter != null) {
                    eventAdapter.clear();
                }
                */

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
                                arrayList.add(new MyEvent(eventName, eventTime, dataSnapshot.getKey(), snapshot.getKey(), creatorName, creatorUid));
                            }
                            else {
                                arrayList.add(new MyEvent(eventName, eventTime, dataSnapshot.getKey(), snapshot.getKey(), Uri.parse(eventCover), creatorName, creatorUid));
                            }
                            if(getActivity() != null) {
                                eventAdapter = new MyEventAdapter(getActivity(), arrayList);
                                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                recyclerView.setAdapter(eventAdapter);
                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        super.onActivityCreated(savedInstanceState);
    }
}
