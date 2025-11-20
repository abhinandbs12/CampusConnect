package com.vineet.campusconnect;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vineet.campusconnect.adapters.EventAdapter;
import com.vineet.campusconnect.models.Event;

import java.util.ArrayList;
import java.util.List;

public class EventTrackerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_tracker);

        // 1. Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_events);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 2. Setup Ongoing List
        RecyclerView recyclerOngoing = findViewById(R.id.recycler_ongoing);
        recyclerOngoing.setLayoutManager(new LinearLayoutManager(this));

        List<Event> ongoingEvents = new ArrayList<>();
        // DATA REQUESTED BY YOU:
        ongoingEvents.add(new Event(
                "Mobile Application Development Expo",
                "21 November 2025",
                "10:00 AM - 3:00 PM",
                "Seminar Hall",
                "ONGOING"
        ));

        recyclerOngoing.setAdapter(new EventAdapter(ongoingEvents));

        // 3. Setup Upcoming List
        RecyclerView recyclerUpcoming = findViewById(R.id.recycler_upcoming);
        recyclerUpcoming.setLayoutManager(new LinearLayoutManager(this));

        List<Event> upcomingEvents = new ArrayList<>();
        // DATA REQUESTED BY YOU:
        upcomingEvents.add(new Event(
                "Hackathon 2025",
                "Monday, 24 November 2025",
                "9:00 AM Start",
                "D block Ground Floor",
                "UPCOMING"
        ));

        recyclerUpcoming.setAdapter(new EventAdapter(upcomingEvents));
    }
}