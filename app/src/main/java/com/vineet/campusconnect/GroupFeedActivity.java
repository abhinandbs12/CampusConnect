package com.vineet.campusconnect; // <-- Make sure this line matches!

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class GroupFeedActivity extends AppCompatActivity {

    // 1. Declare our UI elements
    RecyclerView recyclerView;
    FloatingActionButton fab;
    Toolbar toolbar;

    // 2. Declare Firebase variables
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<GroupListing, GroupViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_feed);

        // 3. Find all the UI elements
        recyclerView = findViewById(R.id.recycler_view_feed);
        fab = findViewById(R.id.fab_create_listing);
        toolbar = findViewById(R.id.toolbar_group_feed);

        // 4. Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // 5. Set up the toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 6. Set click listener for the FAB (the + button)
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupFeedActivity.this, CreateListingActivity.class);
                startActivity(intent);
            }
        });

        // 7. Call the new method to set up the RecyclerView
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        // 8. Define the Query: Get all listings, newest first
        Query query = db.collection("group_listings")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        // 9. Configure the Adapter options
        FirestoreRecyclerOptions<GroupListing> options =
                new FirestoreRecyclerOptions.Builder<GroupListing>()
                        .setQuery(query, GroupListing.class)
                        .build();

        // 10. Create the Adapter
        adapter = new FirestoreRecyclerAdapter<GroupListing, GroupViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull GroupViewHolder holder, int position, @NonNull GroupListing model) {
                // This is where you bind the data to the card
                holder.tvTitle.setText(model.getTitle());
                holder.tvAuthor.setText("Posted by " + model.getOwnerName());

                // Calculate member count
                int memberCount = model.getMembers() != null ? model.getMembers().size() : 0;
                String memberText = "Members: " + memberCount + " / " + model.getMaxMembers();
                holder.tvMembers.setText(memberText);

                // --- THIS IS THE NEW CLICK LISTENER ---
                // Get the Document ID for the clicked item
                String documentId = getSnapshots().getSnapshot(position).getId();

                // Set a click listener on the entire card
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Create an Intent to open GroupDetailsActivity
                        Intent intent = new Intent(GroupFeedActivity.this, GroupDetailsActivity.class);

                        // Pass the document ID to the new activity
                        intent.putExtra("LISTING_ID", documentId);

                        startActivity(intent);
                    }
                });
                // --- END OF NEW CODE ---
            }

            @NonNull
            @Override
            public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // This "inflates" your item_group_listing.xml layout
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_group_listing, parent, false);
                return new GroupViewHolder(view);
            }
        };

        // 11. Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    // 12. Tell the adapter to start/stop listening when the Activity does
    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}