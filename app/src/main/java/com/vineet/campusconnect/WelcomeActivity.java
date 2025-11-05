package com.vineet.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView; // Added import for ImageView
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ImageView logoImage; // Declare logo image view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // 1. Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 2. Find the "Continue" button and logo image
        Button continueButton = findViewById(R.id.continue_button);
        logoImage = findViewById(R.id.logo_image); // Find the ImageView by its ID

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

        // 4. Implement the Fading Animation
        logoImage.animate()
                .alpha(1.0f)     // Fade to fully visible
                .setDuration(1500) // Over 1.5 seconds
                .start();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // THIS IS THE AUTO-LOGIN CHECK
        // Check if a user is currently signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is already logged in!
            // Skip the Welcome screen and go straight to MainActivity
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close the WelcomeActivity
        }
        // If currentUser is null, the app will stay on WelcomeActivity
        // and wait for the user to click "Continue"
    }
}