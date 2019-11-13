package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText editEmail, editUserName, editPassword;
    private Button btnSignUp, btnSignIn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editEmail = findViewById(R.id.editEmail);
        editUserName = findViewById(R.id.editUserName);
        editPassword = findViewById(R.id.editPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            //Transition to next Activity
            transitionToSocialMediaActivity();

        }

    }

    private void signUp() {
        mAuth.createUserWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Signing up Successful", Toast.LENGTH_LONG).show();
                    FirebaseDatabase.getInstance().getReference().child("my_users").child(task.getResult().getUser().getUid()).child("username").setValue(editUserName.getText().toString());

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(editUserName.getText().toString())
                            .build();

                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                       Toast.makeText(MainActivity.this, "Display name updated", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    transitionToSocialMediaActivity();

                } else {
                    Toast.makeText(MainActivity.this, "Signing up Failed", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void signIn() {
        mAuth.signInWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Signing in Successful", Toast.LENGTH_LONG).show();
                    transitionToSocialMediaActivity();

                } else {
                    Toast.makeText(MainActivity.this, "Signing in Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void transitionToSocialMediaActivity() {
        Intent intent = new Intent(this, SocialMediaActivity.class);
        startActivity(intent);
    }
}
