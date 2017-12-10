package com.doenough.contestapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.ArrayList;
import java.util.List;

public class PersonDetailActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 234;

    public static final String STORAGE_PATH_UPLOADS = "paticipants/";

    public static final String STORAGE_PATH_EVENT = "events/";

    //uri to store file
    private Uri filePath[];

    private String eventID;
    private List<EditText> allEds;
    private List<Button> allSelect;
    private List<ImageView> imageViews;
    private List<LinearLayout> linearLayoutList;
    private Button select;
    private ImageView selectedImage;
    private ProgressDialog progressDialog;

    private List<DatabaseReference> databaseReferenceList;
    private FirebaseDatabase firebaseDatabase;

    private StorageReference storageReference;

    private DatabaseReference databaseReference;
    private DatabaseReference anotherReference;


    private DatabaseReference userRef;
    private DatabaseReference tempref;
    private FirebaseUser user;

    private Uri path;
    private String eventName;
    private long eventTime;

    private boolean checker;
    private int k, l;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);
        Bundle bundle = getIntent().getExtras();
        final int participants = Integer.parseInt(bundle.getString("participants"));
        eventID = bundle.getString("eventid");
        eventName = bundle.getString("eventname");
        eventTime = bundle.getLong("eventtime");
        if(bundle.getString("eventimgurl") != null)
        path = Uri.parse(bundle.getString("eventimgurl"));
        Log.e("Path perosnal", path.toString());
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        Button finishButton = (Button) findViewById(R.id.finish_button);
        progressDialog = new ProgressDialog(PersonDetailActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("contest").child(eventID);
        EditText ed;
        LinearLayout layout;
        allEds = new ArrayList<>();
        allSelect = new ArrayList<>();
        imageViews = new ArrayList<>();
        linearLayoutList = new ArrayList<>();
        databaseReferenceList = new ArrayList<>();

        int j;
        for (int i = 0; i < participants; i++) {
            ed = new EditText(PersonDetailActivity.this);
            layout = new LinearLayout(PersonDetailActivity.this);
            select = new Button(PersonDetailActivity.this);
            selectedImage = new ImageView(PersonDetailActivity.this);
            tempref = databaseReference.child("participants").push();
            allEds.add(ed);
            allSelect.add(select);
            imageViews.add(selectedImage);
            linearLayoutList.add(layout);
            j = i + 1;
            ed.setHint("Participant Name " + j);
            select.setText("Select");
            layout.setOrientation(LinearLayout.HORIZONTAL);
            select.setId(i);
            ed.setId(i);
            ed.setMinEms(2);
            ed.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            final int index = i;
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    k = index;
                    showFileChooser();
                }
            });
            LinearLayout.LayoutParams layoutParamsED = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParamsImageView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParamsButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsButton.weight = 3;
            layoutParamsED.weight = 1;
            ed.setLayoutParams(layoutParamsED);
            select.setLayoutParams(layoutParamsButton);
            selectedImage.setLayoutParams(layoutParamsImageView);
            layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.addView(ed);
            layout.addView(select);
            linearLayout.addView(selectedImage);
            linearLayout.addView(layout);
            databaseReferenceList.add(tempref);
        }

        filePath = new Uri[allEds.size()];


        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                databaseReference.child("name").setValue(eventName);
                databaseReference.child("createdat").setValue(eventTime);
                databaseReference.child("nameofcreator").setValue(user.getDisplayName());
                databaseReference.child("uidofcreator").setValue(user.getUid());
                if( path != null ) {
                    uploadEventCover();
                }
                String strings[] = new String[allEds.size()];
                for (int i = 0; i < allEds.size(); i++) {
                    anotherReference = databaseReferenceList.get(i);
                    strings[i] = allEds.get(i).getText().toString();
                    for (int check = 0; check<filePath.length; check++) {
                        if(filePath[check] == null) {
                            checker = true;
                        }
                    }

                    if(filePath[i] != null) {
                        uploadFile(i);
                    }

                    if (strings[i].length() > 0) {
                        anotherReference.child("name").setValue(strings[i]);
                        anotherReference.child("vote").setValue(0);
                    } else {
                        allEds.get(i).setError("Name should not be Empty.");
                        progressDialog.dismiss();
                        return;
                    }
                }

                userRef = firebaseDatabase.getReference("users").child(user.getUid()).child("myevents").push();
                userRef.child("eventid").setValue(databaseReference.getKey());

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
            filePath[k] = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath[k]);
                imageViews.get(k).setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(final int l) {
        //checking if file is available
        if (filePath[l] != null) {
            //displaying progress dialog while image is uploading


            //getting the storage reference
            StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + eventID + "+" + databaseReferenceList.get(l).getKey() + "." + getFileExtension(filePath[l]));
            //adding the file to reference
            sRef.putFile(filePath[l])
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog

                            Log.e("TAG", "Upload Success");
                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            databaseReferenceList.get(l).child("imgurl").setValue(downloadUrl.toString());
                            if(l == allEds.size() - 1) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(PersonDetailActivity.this, EventsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            Log.e("TAG", "Upload Entry Success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();

                            Log.e("TAG", "Upload Failed " + exception);
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            progressDialog.dismiss();
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            Log.e("TAG", "Uploading " + progress);

                        }
                    });
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Please Select a Image", Toast.LENGTH_SHORT).show();
            //display an error if no file is selected
        }
    }

    private void uploadEventCover() {
        //checking if file is available
        if (path != null && user != null) {
            //displaying progress dialog while image is uploading

            //getting the storage reference
            StorageReference sRef = storageReference.child(STORAGE_PATH_EVENT + eventID + "." + getFileExtension(path));
            //adding the file to reference
            sRef.putFile(path)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            Log.e("TAG", "Upload Success");
                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            databaseReference.child("imageurl").setValue(downloadUrl.toString());
                            if (checker) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(PersonDetailActivity.this, EventsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            Log.e("TAG", "Upload Entry Success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("TAG", "Upload Failed " + exception);
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            Log.e("TAG", "Uploading " + progress);
                        }
                    });
        } else {
            Toast.makeText(this, "Please Select a Image", Toast.LENGTH_SHORT).show();
            //display an error if no file is selected
        }
    }


}
