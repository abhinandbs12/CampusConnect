package com.vineet.campusconnect; // <-- Make sure this line matches!

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    // 1. Declare all our new UI elements
    MaterialButtonToggleGroup toggleGroup;
    GridLayout gridUtility, gridPeer;
    ImageButton profileButton;
    MaterialCardView cardCanteen, cardEvent, cardTask, cardLinks;
    TextView welcomeTitle;

    // ... after your other card declarations
    MaterialCardView cardDoubt, cardGroup, cardLostFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2. Link this Java file to our new layout
        setContentView(R.layout.activity_main);

        // 3. Find all the UI elements by their ID
        toggleGroup = findViewById(R.id.toggle_group);
        gridUtility = findViewById(R.id.grid_utility);
        gridPeer = findViewById(R.id.grid_peer);
        profileButton = findViewById(R.id.btn_profile);
        welcomeTitle = findViewById(R.id.tv_welcome_title);



        cardCanteen = findViewById(R.id.card_canteen);
        cardEvent = findViewById(R.id.card_event);
        cardTask = findViewById(R.id.card_task);
        cardLinks = findViewById(R.id.card_links);

        // ... after finding your other cards
        cardDoubt = findViewById(R.id.card_doubt);
        cardGroup = findViewById(R.id.card_group);
        cardLostFound = findViewById(R.id.card_lost_found);


        // 4. Set up the Toggle Button listener
        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.btn_toggle_utility) {
                        // Show Utility grid, hide Peer grid
                        gridUtility.setVisibility(View.VISIBLE);
                        gridPeer.setVisibility(View.GONE);
                    } else if (checkedId == R.id.btn_toggle_peer) {
                        // Show Peer grid, hide Utility grid
                        gridUtility.setVisibility(View.GONE);
                        gridPeer.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        // 5. Set up click listeners for all the cards

        // --- Canteen Card ---
        cardCanteen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This opens your CanteenActivity
                Intent intent = new Intent(MainActivity.this, CanteenActivity.class);
                startActivity(intent);
            }
        });

        // --- Event Card ---
        cardEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComingSoonToast();
            }
        });

        // --- Task Card ---
        cardTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComingSoonToast();
            }
        });

        // --- Links Card ---
        cardLinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComingSoonToast();
            }
        });

        // --- Profile Button ---
        // --- Profile Button ---
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This opens your ProfileActivity
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        // ... after your profileButton.setOnClickListener

        // --- Doubt Forum Card ---
        cardDoubt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComingSoonToast();
            }
        });

        // --- Group Finder Card ---
        // --- Group Finder Card ---
        cardGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This opens your new GroupFeedActivity (the feed)
                Intent intent = new Intent(MainActivity.this, GroupFeedActivity.class);
                startActivity(intent);
            }
        });

        // --- Lost & Found Card ---
        cardLostFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComingSoonToast();
            }
        });

    }

    // A helper method to show a "Coming Soon" message
    private void showComingSoonToast() {
        Toast.makeText(MainActivity.this, "Feature coming soon!", Toast.LENGTH_SHORT).show();
    }
}