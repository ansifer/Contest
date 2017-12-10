package com.doenough.contestapp.participants;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.doenough.contestapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by sanchit on 23/11/17.
 */

public class ParticipantHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context mContext;
    private TextView nameTextView;
    private TextView votesTextView;
    private ImageView imageView;
    private Participant mParticipant;
    private FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference restrictReference;
    private RecyclerView recyclerView;
    private DatabaseReference userReference;
    private String mEventID;



    public ParticipantHolder(String eventid, Context context,final View itemView) {
        super(itemView);
        mEventID = eventid;
        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("contest").child(eventid).child("participants");
        restrictReference = firebaseDatabase.getReference("contest").child(eventid).child("restrict");;
        userReference = firebaseDatabase.getReference("users").child(user.getUid()).child("voted").push().child("eventid");
        nameTextView = itemView.findViewById(R.id.participant_name);
        votesTextView = itemView.findViewById(R.id.participant_votes);
        imageView = itemView.findViewById(R.id.participant_image);
        mContext = context;
        recyclerView = new RecyclerView(context);
        restrictReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user.getUid())) {
                    Toast.makeText(mContext,"Voting is Closed for You.",Toast.LENGTH_SHORT).show();
                }
                else {
                    itemView.setOnClickListener(ParticipantHolder.this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void bindParticipant(Participant participant) {
        mParticipant = participant;
        nameTextView.setText(participant.getmName());
        votesTextView.setText(Long.toString(participant.getmVote()));
        Glide.with(mContext).load(participant.getmImageUrl()).into(imageView);
    }

    @Override
    public void onClick(View v) {
        DatabaseReference voteref = reference.child(mParticipant.getmKey()).child("vote");
        voteref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long votes = dataSnapshot.getValue(long.class);
                reference.child(mParticipant.getmKey()).child("vote").setValue(votes+1);
                restrictReference.child(user.getUid()).setValue(true);
                userReference.setValue(mEventID);
                recyclerView.removeAllViewsInLayout();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
