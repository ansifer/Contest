package com.doenough.contestapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;

public class NewContestActivty extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 234;

    public static final String STORAGE_PATH_UPLOADS = "paticipants/";

    //uri to store file
    private Uri filePath;

    private StorageReference storageReference;


    private EditText contestNameEditText;
    private EditText contestParticipantsEditText;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private long time;

    private ImageView imageView;
    private Button selectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contest_activty);
        contestNameEditText = (EditText) findViewById(R.id.new_contest_name);
        contestParticipantsEditText = (EditText) findViewById(R.id.new_contest_participants);
        Button nextButton = (Button) findViewById(R.id.new_contest_next_button);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if(user == null) {
            startActivity(new Intent(NewContestActivty.this, LoginActivity.class));
            finish();
        }
        imageView = findViewById(R.id.event_image);
        selectButton = findViewById(R.id.event_picture_select);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("contest").push();
        time = Calendar.getInstance().getTimeInMillis();
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewContestActivty.this,PersonDetailActivity.class);
                intent.putExtra("participants", contestParticipantsEditText.getText().toString());
                intent.putExtra("eventid", databaseReference.getKey());
                intent.putExtra("eventname", contestNameEditText.getText().toString());
                intent.putExtra("eventtime", time);
                Log.e("TAG", "FIle Path: "+filePath);
                intent.putExtra("eventimgurl", filePath.toString());
                startActivity(intent);
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });


    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }




}
