package com.vineet.campusconnect.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vineet.campusconnect.R; // <-- THIS LINE MIGHT BE RED.

public class DashboardFragment extends Fragment {

    // 1. Create variables for our database and TextViews
    private FirebaseFirestore db;
    private TextView mainCanteenTextView;
    private TextView foodCourtTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 2. This line connects our Java file to our XML layout file
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // 3. Find the TextViews from our layout using their IDs
        mainCanteenTextView = view.findViewById(R.id.tv_main_canteen_menu);
        foodCourtTextView = view.findViewById(R.id.tv_food_court_menu);

        // 4. Get an instance of our Firestore database
        db = FirebaseFirestore.getInstance();

        // 5. Call the function to fetch the menu data
        fetchCanteenMenu();

        return view;
    }

    private void fetchCanteenMenu() {
        // 6. Go to the "admin_content" collection and get the "canteen" document
        db.collection("admin_content").document("canteen")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 7. If successful, get the document
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // 8. Get the text from the "mainCanteenMenu" and "foodCourtMenu" fields
                                String mainCanteenMenu = document.getString("mainCanteenMenu");
                                String foodCourtMenu = document.getString("foodCourtMenu");

                                // 9. Set the text on our TextViews
                                mainCanteenTextView.setText(mainCanteenMenu);
                                foodCourtTextView.setText(foodCourtMenu);

                            } else {
                                Log.d("Firestore", "No such document");
                                mainCanteenTextView.setText("Menu not found.");
                                foodCourtTextView.setText("Menu not found.");
                            }
                        } else {
                            // 10. If it fails (e.g., no internet), show an error
                            Log.e("Firestore", "Error getting document: ", task.getException());
                            mainCanteenTextView.setText("Failed to load menu.");
                            foodCourtTextView.setText("Failed to load menu.");
                        }
                    }
                });
    }
}