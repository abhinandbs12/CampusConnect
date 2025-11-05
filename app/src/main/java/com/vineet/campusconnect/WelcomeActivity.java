package com.vineet.campusconnect; // <-- Make sure this line matches!

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // 1. Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 2. Find the "Continue" button
        Button continueButton = findViewById(R.id.continue_button);

        // 3. Set the click listener
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When clicked, just go to the LoginActivity
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close the welcome screen
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 4. THIS IS THE AUTO-LOGIN CHECK
        // Check if a user is currently signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is already logged in!
            // Skip the Login screen and go straight to MainActivity
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close the WelcomeActivity
        }
        // If currentUser is null, the app will just stay on WelcomeActivity
        // and wait for the user to click "Continue"
    }
}