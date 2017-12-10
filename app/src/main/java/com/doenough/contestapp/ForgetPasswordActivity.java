package com.doenough.contestapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText forgetEmail;
    private FirebaseAuth mAuth;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        mAuth = FirebaseAuth.getInstance();
        forgetEmail = (EditText) findViewById(R.id.forget_email);
        Button resetPasswordButton = (Button) findViewById(R.id.reset_password_button);
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = forgetEmail.getText().toString();
                resetPassword(email);
            }
        });


    }

    private void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(ForgetPasswordActivity.this, "Reset Password Link Send to your email.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(ForgetPasswordActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
