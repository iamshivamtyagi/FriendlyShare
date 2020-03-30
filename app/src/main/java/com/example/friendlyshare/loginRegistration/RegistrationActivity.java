package com.example.friendlyshare.loginRegistration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.friendlyshare.MainActivity;
import com.example.friendlyshare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class RegistrationActivity extends AppCompatActivity {

    private Button mRegistration;

    private EditText mEmail, mPassword, mName;

    private TextView mSignInFromRegistration;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mSignInFromRegistration = findViewById(R.id.sign_in_text);

        mSignInFromRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mAuth = FirebaseAuth.getInstance();

        mRegistration = findViewById(R.id.registration);
        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);

        // triggered when registration button pressed
        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // user name fetch from mName editText
                final String name = mName.getText().toString();
                // user email fetch from mEmail editText
                final String email = mEmail.getText().toString();
                // user password fetch from mPassword editText
                final String password = mPassword.getText().toString();

                // to register the user on firebase database
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // if not successful simply show toast of error
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplication(), "Registration ERROR", Toast.LENGTH_SHORT).show();
                        } else {
                            // to get unique firebase user Uid
                            String userId = mAuth.getCurrentUser().getUid();

                            // this is to create child of user in the firebase database with unique id (Uid)
                            // data saved in firebase database as :
                            // Application_Firebase_Unique_Id
                                                            // Users
                                                                    // User1
                                                                            // Email
                                                                            // Name
                                                                            // Photo
                                                                    // User2
                                                                            // Email
                                                                            // Name
                                                                            // Photo
                                                                    // Multiple Users

                            // i.e., connecting realtime database with unique authentication Uid
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

                            // creating HashMap to stor e data
                            Map userInfo = new HashMap<>();
                            userInfo.put("email", email);
                            userInfo.put("name", name);
                            userInfo.put("profileImageUrl", "default");

                            // to update the database
                            currentUserDb.updateChildren(userInfo);
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
