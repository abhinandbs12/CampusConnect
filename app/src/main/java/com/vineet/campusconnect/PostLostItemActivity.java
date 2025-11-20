package com.vineet.campusconnect;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vineet.campusconnect.models.LostFoundItem;

import java.util.UUID;

public class PostLostItemActivity extends AppCompatActivity {

    // UI Elements
    private AutoCompleteTextView dropdownStatus;
    private TextInputEditText etTitle, etLocation, etDescription, etContact;
    private MaterialButton btnAttachImage, btnPostItem;
    private ImageView ivImagePreview;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private String currentUserName = "Anonymous";

    // Image Data
    private Uri selectedImageUri = null;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private String selectedStatus = "LOST"; // Default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_lost_item);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();
        loadUserName();

        // Find Views
        dropdownStatus = findViewById(R.id.dropdown_status);
        etTitle = findViewById(R.id.et_item_title);
        etLocation = findViewById(R.id.et_item_location);
        etDescription = findViewById(R.id.et_item_description);
        etContact = findViewById(R.id.et_item_contact);
        btnAttachImage = findViewById(R.id.btn_attach_item_image);
        btnPostItem = findViewById(R.id.btn_post_item);
        ivImagePreview = findViewById(R.id.iv_item_image_preview);

        // Setup Dropdown
        String[] statuses = new String[]{"Lost Item", "Found Item"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        dropdownStatus.setAdapter(adapter);
        dropdownStatus.setOnItemClickListener((parent, view, position, id) -> {
            selectedStatus = (position == 0) ? "LOST" : "FOUND";
        });

        // Setup Image Picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        ivImagePreview.setVisibility(View.VISIBLE);
                        ivImagePreview.setImageURI(uri);
                        btnAttachImage.setText("Change Image");
                    }
                });

        // Button Click Listeners
        btnAttachImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnPostItem.setOnClickListener(v -> validateAndPost());
    }

    private void loadUserName() {
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            currentUserName = documentSnapshot.getString("name");
                        }
                    });
        }
    }

    private void validateAndPost() {
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String contact = etContact.getText().toString().trim();

        if (title.isEmpty() || location.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill Title, Location, and Contact fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to post.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPostItem.setEnabled(false);
        btnPostItem.setText("Posting...");

        if (selectedImageUri != null) {
            uploadImageAndPost(title, description, location, contact, selectedStatus);
        } else {
            postToFirestore(title, description, location, contact, selectedStatus, null);
        }
    }

    private void uploadImageAndPost(String title, String description, String location, String contact, String status) {
        String filename = UUID.randomUUID().toString();
        StorageReference ref = storage.getReference().child("lost_and_found/" + filename);

        ref.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        postToFirestore(title, description, location, contact, status, imageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnPostItem.setEnabled(true);
                    btnPostItem.setText("Submit Post");
                });
    }

    private void postToFirestore(String title, String description, String location, String contact, String status, String imageUrl) {
        LostFoundItem item = new LostFoundItem(
                title,
                description,
                location,
                imageUrl,
                contact,
                status,
                currentUser.getUid(),
                currentUserName
        );

        db.collection("lost_and_found")
                .add(item)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(PostLostItemActivity.this, "Item posted successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnPostItem.setEnabled(true);
                    btnPostItem.setText("Submit Post");
                });
    }
}