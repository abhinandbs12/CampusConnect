package com.vineet.campusconnect; // <-- Make sure this line matches!

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    // 1. Declare UI and Firebase variables
    TextView tvName, tvEmail, tvBranch;
    MaterialButton logoutButton;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 2. Find UI elements
        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);
        tvBranch = findViewById(R.id.tv_profile_branch);
        logoutButton = findViewById(R.id.btn_logout);

        // 3. Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // 4. Load user data
        if (currentUser != null) {
            loadUserProfileData();
        } else {
            // User is somehow not logged in, send them to login
            goToLogin();
        }

        // 5. Set click listener for Logout Button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign the user out
                mAuth.signOut();
                Toast.makeText(ProfileActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                // Send them back to the Login screen
                goToLogin();
            }
        });
    }

    private void loadUserProfileData() {
        String uid = currentUser.getUid();

        // Set the email (we get this from Auth, not Firestore)
        tvEmail.setText(currentUser.getEmail());

        // Get the rest of the data (Name, Branch) from Firestore
        db.collection("users").document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Data found! Set the text
                                String name = document.getString("name");
                                String branch = document.getString("branch");

                                tvName.setText(name);
                                tvBranch.setText(branch);

                            } else {
                                Log.d("Profile", "No user data found in Firestore");
                                tvName.setText("No name found");
                                tvBranch.setText("No branch found");
                            }
                        } else {
                            // Error getting document
                            Log.w("Profile", "Error getting document: ", task.getException());
                            Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void goToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        // Clear all previous activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close the ProfileActivity
    }
}