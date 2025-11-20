package com.vineet.campusconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation; // Import
import android.view.animation.Animation; // Import
import android.view.animation.AnimationSet; // Import
import android.view.animation.TranslateAnimation; // Import
import android.widget.GridLayout; // Import
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vineet.campusconnect.*;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private ChipGroup chipGroupMainFilter;
    private ImageButton btnMenu;
    private TextView tvDynamicTitle;
    private FirebaseUser currentUser;
    private GridLayout mainCardGrid; // Reference to the Grid

    private MaterialCardView cardCanteen, cardTask, cardLinks, cardEvent;
    private MaterialCardView cardDoubt, cardGroup, cardLostFound;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. Find Views
        chipGroupMainFilter = view.findViewById(R.id.chip_group_main_filter);
        btnMenu = view.findViewById(R.id.btn_menu);
        tvDynamicTitle = view.findViewById(R.id.tv_dynamic_title);
        mainCardGrid = view.findViewById(R.id.main_card_grid); // Find the Grid

        cardCanteen = view.findViewById(R.id.card_canteen);
        cardTask = view.findViewById(R.id.card_task);
        cardLinks = view.findViewById(R.id.card_links);
        cardEvent = view.findViewById(R.id.card_event);
        cardDoubt = view.findViewById(R.id.card_doubt);
        cardGroup = view.findViewById(R.id.card_group);
        cardLostFound = view.findViewById(R.id.card_lost_found);

        // 2. Load User & Greeting
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            loadUserName();
        } else {
            tvDynamicTitle.setText(getGreetingMessage() + " Student, ease your uni stuff");
        }

        // 3. Filter Toggle
        chipGroupMainFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_filter_utility) {
                filterCards("utility");
                animateDashboard(); // Re-animate when switching tabs!
            }
            else if (checkedId == R.id.chip_filter_peer) {
                filterCards("peer");
                animateDashboard(); // Re-animate
            }
        });

        if (chipGroupMainFilter.getCheckedChipId() == View.NO_ID) {
            chipGroupMainFilter.check(R.id.chip_filter_utility);
        } else {
            if (chipGroupMainFilter.getCheckedChipId() == R.id.chip_filter_utility) filterCards("utility");
            else filterCards("peer");
        }

        // 4. Setup Clicks (Unchanged)
        btnMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).openDrawer();
        });

        cardCanteen.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_canteen));
        cardTask.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_task_manager));
        cardLinks.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_link_categories));
        cardEvent.setOnClickListener(v -> startActivity(new Intent(getActivity(), EventTrackerActivity.class)));
        cardDoubt.setOnClickListener(v -> startActivity(new Intent(getActivity(), DoubtFeedActivity.class)));
        cardGroup.setOnClickListener(v -> startActivity(new Intent(getActivity(), GroupFeedActivity.class)));
        cardLostFound.setOnClickListener(v -> startActivity(new Intent(getActivity(), LostAndFoundActivity.class)));

        // 5. Trigger Initial Animation
        animateDashboard();

        return view;
    }

    // --- NEW: PROFESSIONAL STAGGERED ANIMATION ---
    private void animateDashboard() {
        // Loop through all child cards in the Grid
        int count = mainCardGrid.getChildCount();
        int animationDelay = 0;

        for (int i = 0; i < count; i++) {
            View child = mainCardGrid.getChildAt(i);

            // Only animate visible cards
            if (child.getVisibility() == View.VISIBLE) {

                // Create Fade In
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setDuration(500);

                // Create Slide Up
                Animation slideUp = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_PARENT, 0.2f, // Start 20% lower
                        Animation.RELATIVE_TO_SELF, 0);
                slideUp.setDuration(500);

                // Combine them
                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(fadeIn);
                animationSet.addAnimation(slideUp);
                animationSet.setStartOffset(animationDelay); // Stagger effect

                child.startAnimation(animationSet);

                // Increase delay for next card (cascading effect)
                animationDelay += 100;
            }
        }
    }

    // ... (Rest of your existing methods: switchToMode, loadUserName, getGreetingMessage, filterCards) ...
    // Make sure to keep them!

    public void switchToMode(String mode) {
        if (mode.equals("peer")) {
            chipGroupMainFilter.check(R.id.chip_filter_peer);
        } else {
            chipGroupMainFilter.check(R.id.chip_filter_utility);
        }
    }

    private void loadUserName() {
        FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String timeGreeting = getGreetingMessage();

                        if (name != null && !name.isEmpty()) {
                            String firstName = name.split(" ")[0];
                            tvDynamicTitle.setText(timeGreeting + " " + firstName + ", ease your uni stuff");
                        } else {
                            tvDynamicTitle.setText(timeGreeting + " Student, ease your uni stuff");
                        }
                    }
                });
    }

    private String getGreetingMessage() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 12) return "Good Morning";
        else if (hour >= 12 && hour < 17) return "Good Afternoon";
        else return "Good Evening";
    }

    private void filterCards(String tag) {
        int utilityVis = tag.equals("utility") ? View.VISIBLE : View.GONE;
        int peerVis = tag.equals("peer") ? View.VISIBLE : View.GONE;

        cardCanteen.setVisibility(utilityVis);
        cardTask.setVisibility(utilityVis);
        cardLinks.setVisibility(utilityVis);
        cardEvent.setVisibility(utilityVis);

        cardDoubt.setVisibility(peerVis);
        cardGroup.setVisibility(peerVis);
        cardLostFound.setVisibility(peerVis);
    }
}