package com.vineet.campusconnect; // <-- Make sure this line matches!

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateListingActivity extends AppCompatActivity {

    // 1. Declare all our UI elements
    TextInputEditText etTitle, etDescription, etMembers, etRequirements;
    MaterialButton publishButton;

    // 2. Declare Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String currentUserName = "Loading..."; // We'll load this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);

        // 3. Find all the UI elements by their ID
        etTitle = findViewById(R.id.et_listing_title);
        etDescription = findViewById(R.id.et_listing_description);
        etMembers = findViewById(R.id.et_listing_members);
        etRequirements = findViewById(R.id.et_listing_requirements);
        publishButton = findViewById(R.id.btn_publish_listing);

        // 4. Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // 5. Load the user's name (we need it for the listing)
        loadUserName();

        // 6. Set click listener for the PUBLISH button
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishListing();
            }
        });
    }

    // Helper method to get the logged-in user's name from Firestore
    private void loadUserName() {
        if (currentUser == null) return; // Not logged in

        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        currentUserName = task.getResult().getString("name");
                        if (currentUserName == null) {
                            currentUserName = "Anonymous";
                        }
                    } else {
                        currentUserName = "Anonymous";
                    }
                });
    }

    // This method contains all the publishing logic
    private void publishListing() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String membersStr = etMembers.getText().toString().trim();
        String requirements = etRequirements.getText().toString().trim();

        // 1. Validation
        if (title.isEmpty() || description.isEmpty() || membersStr.isEmpty() || requirements.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to post", Toast.LENGTH_SHORT).show();
            return;
        }

        int maxMembers;
        try {
            maxMembers = Integer.parseInt(membersStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number for members", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Publishing...", Toast.LENGTH_SHORT).show();

        // 2. Create a data map for Firestore
        HashMap<String, Object> listing = new HashMap<>();
        listing.put("title", title);
        listing.put("description", description);
        listing.put("requirements", requirements); // NEW field
        listing.put("maxMembers", maxMembers);
        listing.put("ownerId", currentUser.getUid());
        listing.put("ownerName", currentUserName); // We loaded this in onCreate
        listing.put("timestamp", FieldValue.serverTimestamp());

        // 3. Create the initial "members" array
        // We add the owner as the first member
        ArrayList<String> membersList = new ArrayList<>();
        membersList.add(currentUser.getUid());
        listing.put("members", membersList);

        // 4. Create the "applicants" map (for resume uploads!)
        // This will store applicants, e.g., {"applicant_uid": "resume_url"}
        HashMap<String, String> applicants = new HashMap<>();
        listing.put("applicants", applicants); // NEW field

        // 5. Save to the "group_listings" collection in Firestore
        db.collection("group_listings")
                .add(listing) // .add() creates a new document with an auto-ID
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateListingActivity.this, "Listing published!", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity and go back to the main menu
                        } else {
                            Log.w("Publish", "Error adding document", task.getException());
                            Toast.makeText(CreateListingActivity.this, "Failed to publish: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}