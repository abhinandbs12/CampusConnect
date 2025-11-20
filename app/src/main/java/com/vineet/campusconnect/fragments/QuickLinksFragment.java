package com.vineet.campusconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vineet.campusconnect.R;
import com.vineet.campusconnect.adapters.LinkAdapter;
import com.vineet.campusconnect.models.LinkItem;

public class QuickLinksFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinkAdapter adapter;
    private FirebaseFirestore db;
    private String currentCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quick_links, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            currentCategory = getArguments().getString("CATEGORY_NAME");
        }
        if (currentCategory == null) currentCategory = "All";

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_quick_links);
        NavController navController = Navigation.findNavController(view);
        NavigationUI.setupWithNavController(toolbar, navController);
        toolbar.setTitle(currentCategory);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycler_view_links);

        // FIX 1: Disable Item Animator to prevent crash on rapid updates/returns
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupRecyclerView(getBaseQuery());

        androidx.appcompat.widget.SearchView searchView = view.findViewById(R.id.search_view_links);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });
    }

    private Query getBaseQuery() {
        if (currentCategory.equals("All")) {
            return db.collection("useful_links").orderBy("title");
        } else {
            return db.collection("useful_links")
                    .whereEqualTo("category", currentCategory)
                    .orderBy("title");
        }
    }

    private void firebaseSearch(String searchText) {
        Query firebaseSearchQuery;
        if (searchText.isEmpty()) {
            firebaseSearchQuery = getBaseQuery();
        } else {
            firebaseSearchQuery = db.collection("useful_links")
                    .orderBy("title")
                    .startAt(searchText)
                    .endAt(searchText + "\uf8ff");
        }
        setupRecyclerView(firebaseSearchQuery);
    }

    private void setupRecyclerView(Query query) {
        // FIX 2: Safely stop the previous adapter before creating a new one
        if (adapter != null) {
            adapter.stopListening();
        }

        FirestoreRecyclerOptions<LinkItem> options = new FirestoreRecyclerOptions.Builder<LinkItem>()
                .setQuery(query, LinkItem.class)
                .build();

        // Create new adapter (context-safe constructor we fixed earlier)
        adapter = new LinkAdapter(options);

        // FIX 3: Set adapter implies a data reset, which avoids the inconsistency crash
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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