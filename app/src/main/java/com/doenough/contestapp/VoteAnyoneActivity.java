package com.doenough.contestapp;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.doenough.contestapp.participants.Participant;
import com.doenough.contestapp.participants.ParticipantAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VoteAnyoneActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private ArrayList<Participant> participants;
    private String eventid;
    private String eventName;
    private long eventTime;
    private Uri eventCover;

    private ParticipantAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_anyone);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //this line shows back button
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        eventid = extras.getString("eventid");
        eventName = extras.getString("eventname");
        eventTime = extras.getLong("eventtime");
        eventCover = (Uri) extras.get("eventcover");
        ImageView imageView = findViewById(R.id.voting_event_cover);
        Glide.with(getApplicationContext()).load(eventCover).into(imageView);
        if(getSupportActionBar()!= null) {
            getSupportActionBar().setTitle(eventName);
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        participants = new ArrayList<>();
        recyclerView = findViewById(R.id.participants_recycler_view);
        recyclerView.setHasFixedSize(true);
        if(user == null)
        {
            startActivity(new Intent(VoteAnyoneActivity.this,LoginActivity.class));
            finish();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("contest").child(eventid).child("participants");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                participants.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    participants.add(new Participant(postSnapshot.child("name").getValue(String.class),
                            postSnapshot.child("vote").getValue(long.class), postSnapshot.getKey(),
                            postSnapshot.child("imgurl").getValue(String.class)));

                }
                adapter = new ParticipantAdapter(eventid, getApplicationContext(),participants);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        finish();
        return true;
    }
}
