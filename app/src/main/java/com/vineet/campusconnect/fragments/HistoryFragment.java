package com.vineet.campusconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar; // If you want a loader

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vineet.campusconnect.DoubtDetailsActivity;
import com.vineet.campusconnect.R;
import com.vineet.campusconnect.adapters.DoubtAdapter;
import com.vineet.campusconnect.models.Doubt;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout emptyStateLayout;
    private Button btnBrowse;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DoubtAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // 1. Initialize Views
        recyclerView = view.findViewById(R.id.recycler_history);
        emptyStateLayout = view.findViewById(R.id.layout_empty_history);
        btnBrowse = view.findViewById(R.id.btn_browse_home);

        // 2. Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // 3. Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 4. Load Data
        setupHistoryList();

        // 5. Handle "Start Exploring" button
        btnBrowse.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_home);
        });

        return view;
    }

    private void setupHistoryList() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        // Query: Get doubts where 'authorId' equals current user's ID
        // Ordered by timestamp (newest first)
        Query query = db.collection("doubts")
                .whereEqualTo("authorId", user.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Doubt> options = new FirestoreRecyclerOptions.Builder<Doubt>()
                .setQuery(query, Doubt.class)
                .build();

        // Initialize Adapter
        adapter = new DoubtAdapter(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                // Toggle Empty State based on item count
                if (getItemCount() == 0) {
                    emptyStateLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        };

        // Handle clicks (Go to Details)
        adapter.setOnDoubtClickListener(doubtId -> {
            Intent intent = new Intent(getContext(), DoubtDetailsActivity.class);
            intent.putExtra("DOUBT_ID", doubtId);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}