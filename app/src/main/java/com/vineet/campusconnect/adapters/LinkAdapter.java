package com.vineet.campusconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.chip.Chip;
import com.vineet.campusconnect.R;
import com.vineet.campusconnect.models.LinkItem;

public class LinkAdapter extends FirestoreRecyclerAdapter<LinkItem, LinkAdapter.LinkViewHolder> {

    public LinkAdapter(@NonNull FirestoreRecyclerOptions<LinkItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull LinkViewHolder holder, int position, @NonNull LinkItem model) {
        holder.tvTitle.setText(model.getTitle());
        holder.tvDescription.setText(model.getDescription());
        holder.chipCategory.setText(model.getCategory());

        if (model.isImportant()) {
            holder.ivImportantBadge.setVisibility(View.VISIBLE);
        } else {
            holder.ivImportantBadge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            // Always get the fresh, live context from the view
            Context context = v.getContext();
            openLink(context, model.getUrl());
        });
    }

    @NonNull
    @Override
    public LinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quick_link, parent, false);
        return new LinkViewHolder(view);
    }

    private void openLink(Context context, String url) {
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(context, Uri.parse(url));
        } catch (Exception e) {
            // Fallback if Chrome is not installed or fails
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                // SAFETY FLAG: Prevents crash if context is not an Activity
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(browserIntent);
            } catch (Exception ex) {
                // Handle invalid URL or no browser found
            }
        }
    }

    static class LinkViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        Chip chipCategory;
        ImageView ivImportantBadge;

        public LinkViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_link_title);
            tvDescription = itemView.findViewById(R.id.tv_link_description);
            chipCategory = itemView.findViewById(R.id.chip_link_category);
            ivImportantBadge = itemView.findViewById(R.id.iv_important_badge);
        }
    }
}