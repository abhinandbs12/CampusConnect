package com.vineet.campusconnect.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue; // Import
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window; // Import
import android.view.WindowManager; // Import
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat; // Import
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vineet.campusconnect.LoginActivity;
import com.vineet.campusconnect.R;

public class ProfileFragment extends Fragment {

    // ... (Existing Variables: db, currentUser, views, etc.) ...
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;

    private TextView tvName, tvEmail, tvBranch, btnRemovePhoto;
    private Button btnLogout;
    private ImageView ivProfileImage;
    private FloatingActionButton fabEditPhoto;
    private ProgressBar progressBar;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // ... (Your existing initialization code: findViewById, firebase init) ...
        tvName = view.findViewById(R.id.tv_profile_name);
        tvEmail = view.findViewById(R.id.tv_profile_email);
        tvBranch = view.findViewById(R.id.tv_profile_branch);
        btnLogout = view.findViewById(R.id.btn_logout);
        ivProfileImage = view.findViewById(R.id.iv_profile_image);
        fabEditPhoto = view.findViewById(R.id.fab_edit_photo);
        btnRemovePhoto = view.findViewById(R.id.btn_remove_photo);
        progressBar = view.findViewById(R.id.progressBar_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> { if (uri != null) uploadProfilePhoto(uri); }
        );

        if (currentUser != null) {
            loadUserProfileData();
            fabEditPhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
            btnRemovePhoto.setOnClickListener(v -> removeProfilePhoto());
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
                goToLogin();
            });
        } else {
            goToLogin();
        }

        return view;
    }

    // --- NEW: Change Status Bar Color to Purple on Resume ---
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // Set color to Purple (colorPrimary)
            TypedValue typedValue = new TypedValue();
            getActivity().getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
            window.setStatusBarColor(typedValue.data);
        }
    }

    // --- NEW: Reset Status Bar Color when leaving ---
    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            Window window = getActivity().getWindow();
            // Reset to default surface color (White/Dark)
            TypedValue typedValue = new TypedValue();
            getActivity().getTheme().resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true);
            window.setStatusBarColor(typedValue.data);
        }
    }

    // ... (Rest of your methods: loadUserProfileData, uploadProfilePhoto, etc.) ...
    // Keep them exactly as they were!

    private void loadUserProfileData() {
        progressBar.setVisibility(View.VISIBLE);
        String uid = currentUser.getUid();
        tvEmail.setText(currentUser.getEmail());

        db.collection("users").document(uid).get().addOnSuccessListener(document -> {
            progressBar.setVisibility(View.GONE);
            if (document.exists()) {
                tvName.setText(document.getString("name"));
                tvBranch.setText(document.getString("branch"));
                String imageUrl = document.getString("profileImageUrl");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this).load(imageUrl).placeholder(R.drawable.img_my_profile).into(ivProfileImage);
                    btnRemovePhoto.setVisibility(View.VISIBLE);
                } else {
                    ivProfileImage.setImageResource(R.drawable.img_my_profile);
                    btnRemovePhoto.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(e -> progressBar.setVisibility(View.GONE));
    }

    private void uploadProfilePhoto(Uri imageUri) {
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), "Uploading...", Toast.LENGTH_SHORT).show();
        String uid = currentUser.getUid();
        StorageReference ref = storage.getReference().child("profile_images/" + uid + ".jpg");
        ref.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                updateFirestoreImage(uri.toString());
            });
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateFirestoreImage(String url) {
        db.collection("users").document(currentUser.getUid()).update("profileImageUrl", url)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Glide.with(this).load(url).into(ivProfileImage);
                    btnRemovePhoto.setVisibility(View.VISIBLE);
                });
    }

    private void removeProfilePhoto() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("users").document(currentUser.getUid()).update("profileImageUrl", null)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    ivProfileImage.setImageResource(R.drawable.img_my_profile);
                    btnRemovePhoto.setVisibility(View.GONE);
                });
    }

    private void goToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) getActivity().finish();
    }
}