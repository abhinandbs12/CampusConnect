package com.vineet.campusconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vineet.campusconnect.R;

import java.util.Calendar;

public class CanteenFragment extends Fragment {

    // Removed FirebaseFirestore db since we are generating menu locally/dynamically
    private TextView mainCanteenTextView;
    private TextView foodCourtTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_canteen, container, false);

        mainCanteenTextView = view.findViewById(R.id.tv_main_canteen_menu);
        foodCourtTextView = view.findViewById(R.id.tv_food_court_menu);

        // Call the function to fetch and set the dynamic menu data
        fetchCanteenMenu();

        return view;
    }

    private void fetchCanteenMenu() {
        // Get the current hour of the day
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String mainCanteenContent;
        String foodCourtContent;

        // --- Main Canteen Menu Logic ---
        if (hour >= 6 && hour < 11) { // Morning: 6:00 AM to 10:59 AM
            mainCanteenContent = "📍 MORNING SPECIALS (7:30 AM - 11:00 AM)\n\n" +
                    "**Breakfast Items**\n" +
                    "• Idly (2 Pc) + 1 Vada - Rs 50\n" +
                    "• Idly (1 Pc) - Rs 15\n" +
                    "• Poha / Upma - Rs 30\n" +
                    "• Tea / Coffee - Rs 10";
        } else if (hour >= 11 && hour < 16) { // Afternoon: 11:00 AM to 3:59 PM
            mainCanteenContent = "📍 AFTERNOON: LUNCH SPECIALS\n\n" +
                    "• Veg Biryani (with Raita) - Rs 120\n" +
                    "• Chicken Biryani (with Gravy) - Rs 150\n" +
                    "• North Indian Thali - Rs 100\n" +
                    "• South Indian Meals - Rs 80";
        } else { // Evening / Closed
            mainCanteenContent = "Main Canteen is closed, please check the Food Court for evening snacks.";
        }

        // --- Food Court Menu Logic ---
        if (hour >= 15 && hour < 20) { // Evening Snacks: 3:00 PM to 7:59 PM
            foodCourtContent = "🧁 EVENING: BAKERY & SNACKS\n\n" +
                    "**Snack Items**\n" +
                    "• Veg Puffs / Egg Puffs - Rs 25\n" +
                    "• Samosa (2 Pc) - Rs 30\n" +
                    "• Paneer Roll - Rs 60\n\n" +
                    "**Desserts / Pastries**\n" +
                    "• Chocolate Pastry - Rs 50\n" +
                    "• Black Forest Slice - Rs 60\n" +
                    "• Donuts - Rs 40";
        } else {
            foodCourtContent = "Food Court is currently closed. Visit during evening snack hours.";
        }

        // Set the dynamic content
        mainCanteenTextView.setText(mainCanteenContent);
        foodCourtTextView.setText(foodCourtContent);
    }
}