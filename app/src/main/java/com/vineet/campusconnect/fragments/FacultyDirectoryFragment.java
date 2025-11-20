package com.vineet.campusconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.vineet.campusconnect.R;
import com.vineet.campusconnect.adapters.FacultyAdapter;
import com.vineet.campusconnect.models.Faculty;

import java.util.ArrayList;
import java.util.List;

public class FacultyDirectoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private FacultyAdapter adapter;
    private List<Faculty> facultyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Reusing the layout
        return inflater.inflate(R.layout.activity_faculty_directory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_faculty);
        recyclerView.setItemAnimator(null); // Prevents crash on back
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 1. Create the Hardcoded Data List
        facultyList = new ArrayList<>();

        // Vineeth S
        facultyList.add(new Faculty(
                "Vineeth S",
                "Assistant Professor",
                "vineeth.s@presidency.edu.in",
                "Computer Science Block",
                null
        ));

        // Blessed Prince P
        facultyList.add(new Faculty(
                "Blessed Prince P",
                "HOD",
                "blessed.prince@presidency.edu.in",
                "LT WS 01",
                null
        ));

        // Saurabh Sarkar
        facultyList.add(new Faculty(
                "Saurabh Sarkar",
                "Associate Professor",
                "saurabh.sarkar@presidency.edu.in",
                "LS WS 05",
                null
        ));

        // 2. Set up Adapter (No Firestore Options needed)
        adapter = new FacultyAdapter(requireContext(), facultyList);

        adapter.setOnItemClickListener(faculty -> {
            Bundle args = new Bundle();
            args.putString("NAME", faculty.getName());
            args.putString("DESIGNATION", faculty.getDesignation());
            args.putString("EMAIL", faculty.getEmail());
            args.putString("CABIN", faculty.getCabinNumber());
            args.putString("IMAGE", faculty.getImageUrl());

            Navigation.findNavController(requireView())
                    .navigate(R.id.action_directory_to_details, args);
        });

        recyclerView.setAdapter(adapter);

        // 3. Setup Search
        SearchView searchView = view.findViewById(R.id.search_view_faculty);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });
    }

    // REMOVED: onStart/onStop with startListening/stopListening
    // Standard RecyclerView Adapters don't need them.
}