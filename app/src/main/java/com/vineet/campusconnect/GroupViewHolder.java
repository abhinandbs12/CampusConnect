package com.vineet.campusconnect; // <-- Make sure this line matches!

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// 1. This class holds the UI (the views) for a single card
public class GroupViewHolder extends RecyclerView.ViewHolder {

    // 2. Declare the UI elements from your item_group_listing.xml
    TextView tvTitle, tvAuthor, tvMembers;

    public GroupViewHolder(@NonNull View itemView) {
        super(itemView);

        // 3. Find the UI elements by their ID
        tvTitle = itemView.findViewById(R.id.tv_listing_title);
        tvAuthor = itemView.findViewById(R.id.tv_listing_author);
        tvMembers = itemView.findViewById(R.id.tv_listing_members);
    }
}