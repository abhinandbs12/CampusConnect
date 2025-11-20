package com.vineet.campusconnect; // <-- Make sure this line matches!

import java.util.ArrayList;

// This is our Model Class (a "POJO")
public class GroupListing {
    // 1. Declare variables that match your Firestore document
    private String title;
    private String ownerName;
    private long maxMembers;
    private ArrayList<String> members; // For the member count

    // 2. IMPORTANT: An empty constructor is required for Firestore
    public GroupListing() {
        // Default constructor required for calls to DataSnapshot.getValue(GroupListing.class)
    }

    // 3. Getters (which the adapter will use to get the data)
    public String getTitle() {
        return title;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public long getMaxMembers() {
        return maxMembers;
    }

    public ArrayList<String> getMembers() {
        return members;
    }
}