package com.vineet.campusconnect.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vineet.campusconnect.R;

import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    private TextInputEditText etName, etBranch, etEmail;
    private Button btnSave;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // 1. Init Views
        etName = view.findViewById(R.id.et_edit_name);
        etBranch = view.findViewById(R.id.et_edit_branch);
        etEmail = view.findViewById(R.id.et_edit_email);
        btnSave = view.findViewById(R.id.btn_save_profile);
        progressBar = view.findViewById(R.id.progressBar_edit);

        // 2. Init Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getCurrentUser().getUid();
            loadUserData();
        }

        // 3. Save Logic
        btnSave.setOnClickListener(v -> saveProfileChanges());

        return view;
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);
        // Set email from Auth directly (most reliable)
        etEmail.setText(mAuth.getCurrentUser().getEmail());

        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    progressBar.setVisibility(View.GONE);
                    if (document.exists()) {
                        String name = document.getString("name");
                        String branch = document.getString("branch");

                        etName.setText(name);
                        etBranch.setText(branch);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProfileChanges() {
        String newName = etName.getText().toString().trim();
        String newBranch = etBranch.getText().toString().trim();

        if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newBranch)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false); // Prevent double clicks

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        updates.put("branch", newBranch);

        db.collection("users").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                    // Go back to Profile Page
                    Navigation.findNavController(requireView()).popBackStack();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(getContext(), "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}