package com.doenough.contestapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationActivity extends AppCompatActivity {

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(VerificationActivity.this, LoginActivity.class));
            finish();
        } else {
            user.reload();
            if (user.isEmailVerified()) {
                startActivity(new Intent(VerificationActivity.this, EventsActivity.class));
                finish();
            }
        }

        sendEmailVerification();
        Button verificationButton = (Button) findViewById(R.id.verification_button);
        verificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmailVerification();
            }
        });

        Button signOutButton = (Button) findViewById(R.id.sign_out_from_verfication);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(VerificationActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verification_button).setEnabled(false);

        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.verification_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(VerificationActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("TAG", "sendEmailVerification", task.getException());
                            Toast.makeText(VerificationActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
