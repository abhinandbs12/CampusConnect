package com.vineet.campusconnect; // <-- Make sure this line matches!

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class GroupDetailsActivity extends AppCompatActivity {

    // 1. Declare all UI elements
    Toolbar toolbar;
    TextView tvTitle, tvAuthor, tvMembers, tvDescription, tvRequirements, tvResumeStatus;
    MaterialButton btnSelectResume, btnApplyResume;

    // 2. Declare Firebase variables
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;
    FirebaseStorage storage;

    // 3. Variables for logic
    private String listingId; // To know which listing we are looking at
    private Uri resumeFileUri = null; // To hold the selected resume file

    // 4. This is the modern way to pick a file
    private ActivityResultLauncher<String> resumePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        // 5. Find all UI elements
        toolbar = findViewById(R.id.toolbar_group_details);
        tvTitle = findViewById(R.id.tv_details_title);
        tvAuthor = findViewById(R.id.tv_details_author);
        tvMembers = findViewById(R.id.tv_details_members);
        tvDescription = findViewById(R.id.tv_details_description);
        tvRequirements = findViewById(R.id.tv_details_requirements);
        tvResumeStatus = findViewById(R.id.tv_resume_status);
        btnSelectResume = findViewById(R.id.btn_select_resume);
        btnApplyResume = findViewById(R.id.btn_apply_resume);

        // 6. Set up the Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // We'll set title manually
        toolbar.setNavigationOnClickListener(v -> finish());

        // 7. Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // 8. Get the Listing ID from the Intent
        listingId = getIntent().getStringExtra("LISTING_ID");
        if (listingId == null || listingId.isEmpty()) {
            Toast.makeText(this, "Error: No listing ID found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 9. Load the data from Firestore
        loadListingDetails();

        // 10. Register the file picker
        registerFilePicker();

        // 11. Set up button click listeners
        btnSelectResume.setOnClickListener(v -> openFilePicker());
        btnApplyResume.setOnClickListener(v -> applyToGroup());
    }

    // --- Data Loading ---
    private void loadListingDetails() {
        db.collection("group_listings").document(listingId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Data found! Set the UI
                            tvTitle.setText(document.getString("title"));
                            tvAuthor.setText("by " + document.getString("ownerName"));
                            tvDescription.setText(document.getString("description"));
                            tvRequirements.setText(document.getString("requirements"));

                            // Set member count
                            int memberCount = ((java.util.ArrayList) document.get("members")).size();
                            long maxMembers = document.getLong("maxMembers");
                            tvMembers.setText(memberCount + " / " + maxMembers + " members");

                        } else {
                            Toast.makeText(this, "Listing not found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Failed to load listing", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // --- File Picker Logic ---
    private void registerFilePicker() {
        resumePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    // This is the callback for when a file is selected
                    if (uri != null) {
                        resumeFileUri = uri;
                        tvResumeStatus.setText("File selected: " + uri.getLastPathSegment());
                    } else {
                        Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openFilePicker() {
        // We only want PDF files
        resumePickerLauncher.launch("application/pdf");
    }

    // --- Apply to Group Logic ---
    private void applyToGroup() {
        if (resumeFileUri == null) {
            Toast.makeText(this, "Please select your resume (PDF) first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Applying...", Toast.LENGTH_SHORT).show();
        btnApplyResume.setEnabled(false); // Disable button

        // 1. Define where the file will be stored
        String uid = currentUser.getUid();
        String fileName = uid + "_" + System.currentTimeMillis() + ".pdf";
        StorageReference storageRef = storage.getReference()
                .child("resumes")
                .child(listingId)
                .child(fileName);

        // 2. Upload the file to Storage
        storageRef.putFile(resumeFileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // File uploaded successfully! Now get the download URL
                    storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // 3. Save the download URL to the listing's "applicants" map
                        saveApplicationToFirestore(downloadUri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Resume upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnApplyResume.setEnabled(true); // Re-enable button
                });
    }

    private void saveApplicationToFirestore(String resumeUrl) {
        String uid = currentUser.getUid();

        // We need to add the user's UID and their resume URL to the "applicants" map
        // Using "FieldValue.update" is complex for maps, so we'll use "merge"
        // Note: This creates a nested map for the applicant

        String applicantKey = "applicants." + uid; // This is how you update a specific map field

        DocumentReference listingRef = db.collection("group_listings").document(listingId);

        listingRef.update(applicantKey, resumeUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Application submitted!", Toast.LENGTH_LONG).show();
                    btnApplyResume.setText("Applied!");
                    btnApplyResume.setEnabled(false);
                    btnSelectResume.setEnabled(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to submit application: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnApplyResume.setEnabled(true); // Re-enable button
                });
    }
}