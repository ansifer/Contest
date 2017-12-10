package com.doenough.contestapp.event;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.doenough.contestapp.DetailActivity;
import com.doenough.contestapp.R;
import com.doenough.contestapp.VoteAnyoneActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sanchit on 22/11/17.
 */

public class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView nameTextView;
    private TextView timeTextView;
    private ImageView imageView;
    private TextView creatorNameTextView;
    private ImageButton actionButton;
    private Context mContext;
    private Event mEvent;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userReference;
    private DatabaseReference contestReference;
    private DatabaseReference restrictReference;
    private DatabaseReference votedReference;
    private FirebaseUser user;
    private AlertDialog alertDialog1;
    private AlertDialog alertDialog2;
    private AlertDialog alertDialog;
    private ProgressDialog progressDialog;

    public EventHolder(Context context, final View itemView) {
        super(itemView);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Removing...");
        progressDialog.setCancelable(false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mContext = context;
        nameTextView = itemView.findViewById(R.id.event_name_list_text_view);
        timeTextView = itemView.findViewById(R.id.event_time_list_text_view);
        imageView = itemView.findViewById(R.id.event_cover);
        creatorNameTextView = itemView.findViewById(R.id.name_of_creator_list_item_view);
        actionButton = itemView.findViewById(R.id.action_on_event);
        itemView.setOnClickListener(this);
    }

    public void bindEvent(Event event) {
        mEvent = event;
        if (event.getmImageUrl() != null)
            Glide.with(mContext).load(mEvent.getmImageUrl()).into(imageView);
        nameTextView.setText(mEvent.getmName());
        timeTextView.setText(getDate(mEvent.getmTime()));
        creatorNameTextView.setText(mEvent.getmNameOfCreator());

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View dialogView = View.inflate(mContext, R.layout.action_event_dialog, null);
                Button removeButton = dialogView.findViewById(R.id.event_remove_button);
                builder.setView(dialogView);

                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                        builder1.setTitle("Alert!");
                        builder1.setMessage("You cannot recover this event after deleting it.");
                        builder1.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.show();
                                removeEvent();
                            }
                        });
                        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog1.dismiss();
                            }
                        });
                        alertDialog1 = builder1.create();
                        alertDialog1.show();
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (mEvent != null) {
            Intent intent = new Intent(mContext, VoteAnyoneActivity.class);
            intent.putExtra("eventid", mEvent.getmKey());
            intent.putExtra("eventname", mEvent.getmName());
            intent.putExtra("eventtime", mEvent.getmTime());
            intent.putExtra("eventcover", mEvent.getmImageUrl());
            intent.putExtra("userkey", mEvent.getmUserKey());
            mContext.startActivity(intent);
        }
    }

    private String getDate(long timeStamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH);
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

    private void removeEvent() {
        Log.e("tag", mEvent.getmUserKey());
        contestReference = firebaseDatabase.getReference("contest").child(mEvent.getmKey());
        userReference = firebaseDatabase.getReference("users")
                .child(user.getUid()).child("myevents").child(mEvent.getmUserKey());
       contestReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    userReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progressDialog.dismiss();
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(mContext);
                                builder2.setTitle("Removed");
                                builder2.setMessage("Successfully Removed Event " + mEvent.getmName());
                                builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alertDialog2.dismiss();
                                    }
                                });
                                alertDialog2 = builder2.create();
                                alertDialog2.show();
                            }
                            else {
                                progressDialog.dismiss();
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(mContext);
                                builder2.setTitle("Sorry");
                                builder2.setMessage("Something Went Wrong, Try Again.");
                                builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alertDialog2.dismiss();
                                    }
                                });
                                alertDialog2 = builder2.create();
                                alertDialog2.show();
                            }
                        }
                    });
                }
                else {
                    progressDialog.dismiss();
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(mContext);
                    builder2.setTitle("Sorry");
                    builder2.setMessage("Something Went Wrong, Try Again.");
                    builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog2.dismiss();
                        }
                    });
                    alertDialog2 = builder2.create();
                    alertDialog2.show();
                }
            }
        });
    }

}
