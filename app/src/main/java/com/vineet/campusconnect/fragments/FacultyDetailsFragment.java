package com.vineet.campusconnect.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.vineet.campusconnect.R;

public class FacultyDetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_faculty_details, container, false);

        if (getArguments() != null) {
            String name = getArguments().getString("NAME");
            String designation = getArguments().getString("DESIGNATION");
            String email = getArguments().getString("EMAIL");
            String cabin = getArguments().getString("CABIN");
            String imageUrl = getArguments().getString("IMAGE");

            TextView tvName = view.findViewById(R.id.tv_detail_name);
            TextView tvDesig = view.findViewById(R.id.tv_detail_designation);
            TextView tvEmail = view.findViewById(R.id.tv_detail_email);
            TextView tvCabin = view.findViewById(R.id.tv_detail_cabin);
            ImageView ivImage = view.findViewById(R.id.iv_detail_image);
            MaterialButton btnContact = view.findViewById(R.id.btn_contact_faculty);

            if (tvName != null) tvName.setText(name);
            if (tvDesig != null) tvDesig.setText(designation);
            if (tvEmail != null) tvEmail.setText(email);
            if (tvCabin != null) tvCabin.setText(cabin);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).placeholder(R.drawable.ic_nav_profile).into(ivImage);
            } else {
                ivImage.setImageResource(R.drawable.ic_nav_profile);
            }

            if (btnContact != null) {
                btnContact.setOnClickListener(v -> {
                    ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Email", email);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext(), "Email copied!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + email));
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        // No email app
                    }
                });
            }
        }
        return view;
    }
}