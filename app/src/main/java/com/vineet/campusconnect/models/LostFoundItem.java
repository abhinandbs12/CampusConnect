package com.vineet.campusconnect.models;

public class LostFoundItem {

    // --- Core Item Details ---
    private String title;
    private String description;
    private String location;
    private String imageUrl; // For the photo
    private String contactInfo; // User's preferred contact (email/phone)
    private long timestamp; // For sorting by date

    // --- Status Fields ---
    private String status; // We will use "LOST" or "FOUND" for the tag
    private boolean isReturned; // For the "Mark as Returned" button

    // --- Author Info ---
    private String authorId;
    private String authorName;

    // 1. Empty constructor for Firestore
    public LostFoundItem() { }

    // 2. Constructor for creating a new item
    public LostFoundItem(String title, String description, String location, String imageUrl,
                         String contactInfo, String status, String authorId, String authorName) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.imageUrl = imageUrl;
        this.contactInfo = contactInfo;
        this.status = status; // "LOST" or "FOUND"
        this.authorId = authorId;
        this.authorName = authorName;
        this.isReturned = false; // Default to NOT returned
        this.timestamp = System.currentTimeMillis();
    }

    // 3. Getters (needed for Firestore adapter)
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getImageUrl() { return imageUrl; }
    public String getContactInfo() { return contactInfo; }
    public long getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public boolean isReturned() { return isReturned; }
    public String getAuthorId() { return authorId; }
    public String getAuthorName() { return authorName; }
}