package com.example.friendlyshare.loginRegistration;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.friendlyshare.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    public static Boolean started = false;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mAuth will contains all information that associated with the user who is currently logged in
        // we can get id of user,email of the user, or anything that is present in database table of user
        mAuth = FirebaseAuth.getInstance();

        // if mAuth is not null, that means user is logged in
        // we simply launch the MainActivity
        if (mAuth.getCurrentUser() != null) {
            // intent to launch MainActivity after successful log in
            Intent intent = new Intent(getApplication(), MainActivity.class);
            // to clear up anything top of this activity i.e., if user log in or out it will clear everything top of this activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        } else {
            // if mAuth is null, that means user is not logged in and therefore launch the LoginRegistration Activity
            Intent intent = new Intent(getApplication(), ChooseLoginRegistrationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }
    }
}
