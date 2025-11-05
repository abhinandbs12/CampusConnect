package com.vineet.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout; // Use standard Android GridLayout
import android.widget.ImageButton;
import android.widget.TextView; // NEW: Import TextView
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    // 1. Declare all our UI elements.
    MaterialButtonToggleGroup toggleGroup;
    GridLayout gridUtility;
    GridLayout gridPeer;
    ImageButton profileButton;
    TextView welcomeTitle; // NEW: Declaration for the header title

    MaterialCardView cardCanteen, cardEvent, cardTask, cardLinks;

    // ... after your other card declarations
    MaterialCardView cardDoubt, cardGroup, cardLostFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 3. Find all the UI elements by their ID
        toggleGroup = findViewById(R.id.toggle_group);

        // Find the new TextView element
        welcomeTitle = findViewById(R.id.tv_welcome_title);

        // FIX: Both containers must be found as GridLayouts
        gridUtility = findViewById(R.id.grid_utility);
        gridPeer = findViewById(R.id.grid_peer);

        profileButton = findViewById(R.id.btn_profile);

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
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // --- Doubt Forum Card ---
        cardDoubt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComingSoonToast();
            }
        });

        // --- Group Finder Card ---
        cardGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComingSoonToast();
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