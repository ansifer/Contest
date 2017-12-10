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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class EditAccountActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 234;
    public static final String STORAGE_PATH_UPLOADS = "users/";
    //uri to store file
    private Uri filePath;
    private StorageReference storageReference;

    private TextView changePhotoButton;
    private TextView emailTextView;
    private TextView phoneTextView;
    private EditText nameEditText;
    private ImageView imageView;
    private ProgressDialog mProgressDialog;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storageReference  = FirebaseStorage.getInstance().getReference();
        changePhotoButton = findViewById(R.id.change_photo_button);
        emailTextView = findViewById(R.id.edit_account_email);
        phoneTextView = findViewById(R.id.edit_account_phone);
        nameEditText = findViewById(R.id.edit_account_name);
        imageView = findViewById(R.id.account_image);
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            nameEditText.setText(user.getDisplayName());
            emailTextView.setText(user.getEmail());
            if(user.getPhoneNumber() == null) {
                phoneTextView.setText("Add Phone Number");
            }
            else {
                phoneTextView.setText(user.getPhoneNumber());
            }
            if(user.getPhotoUrl() != null) {
                Glide.with(getApplicationContext()).load(user.getPhotoUrl()).into(imageView);
            }
        }

        changePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_account_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.confirm_edits) {
            if(filePath == null) {
                updateDetails(nameEditText.getText().toString());
            }
            else {
                uploadFile();
            }
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    private void updateDetails(final String fullname, final Uri imageUri) {

        showProgressDialog();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullname).setPhotoUri(imageUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("jns", "User profile updated.");
                            startActivity(new Intent(EditAccountActivity.this, AccountActivity.class));
                            finish();
                        }
                    }
                });

    }

    private void updateDetails(final String fullname) {

        showProgressDialog();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullname)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("jns", "User profile updated.");
                            startActivity(new Intent(EditAccountActivity.this, AccountActivity.class));
                            finish();
                        }
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

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        //checking if file is available
        if (filePath != null && user != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //getting the storage reference
            StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + user.getUid() + "." + getFileExtension(filePath));
            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();
                            Log.e("TAG", "Upload Success");
                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            updateDetails(nameEditText.getText().toString(), downloadUrl);
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
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            Log.e("TAG", "Uploading " + progress);
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            Toast.makeText(this, "Please Select a Image", Toast.LENGTH_SHORT).show();
            //display an error if no file is selected
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
