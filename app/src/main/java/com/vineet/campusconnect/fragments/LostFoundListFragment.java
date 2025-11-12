package com.vineet.campusconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vineet.campusconnect.R;
import com.vineet.campusconnect.adapters.LostFoundAdapter;
import com.vineet.campusconnect.models.LostFoundItem;

public class LostFoundListFragment extends Fragment {

    private FirebaseFirestore db;
    private LostFoundAdapter adapter;
    private String fragmentStatusType; // Will be "LOST" or "FOUND"

    // This is a "factory" method to create a new fragment
    public static LostFoundListFragment newInstance(String statusType) {
        LostFoundListFragment fragment = new LostFoundListFragment();
        Bundle args = new Bundle();
        args.putString("STATUS_TYPE", statusType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the status ("LOST" or "FOUND") that was passed to us
        if (getArguments() != null) {
            fragmentStatusType = getArguments().getString("STATUS_TYPE");
        }
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the simple layout we just made
        View view = inflater.inflate(R.layout.fragment_lost_found_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_lost_found);

        setupRecyclerView(recyclerView);
        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        // Create the query based on the status ("LOST" or "FOUND")
        Query query = db.collection("lost_and_found")
                .whereEqualTo("status", fragmentStatusType)
                .whereEqualTo("isReturned", false) // Only show items not yet returned
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<LostFoundItem> options = new FirestoreRecyclerOptions.Builder<LostFoundItem>()
                .setQuery(query, LostFoundItem.class)
                .build();

        adapter = new LostFoundAdapter(options, getContext());

        adapter.setOnItemClickListener(documentId -> {
            // We'll build this details page later
            Toast.makeText(getContext(), "Open details for: " + documentId, Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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