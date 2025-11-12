package com.vineet.campusconnect.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.chip.Chip;
import com.vineet.campusconnect.R;
import com.vineet.campusconnect.models.LostFoundItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LostFoundAdapter extends FirestoreRecyclerAdapter<LostFoundItem, LostFoundAdapter.LostFoundViewHolder> {

    private Context context;
    private OnItemClickListener listener;

    public LostFoundAdapter(@NonNull FirestoreRecyclerOptions<LostFoundItem> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull LostFoundViewHolder holder, int position, @NonNull LostFoundItem model) {

        // 1. Set Basic Text
        holder.tvTitle.setText(model.getTitle());
        holder.tvLocation.setText(model.getLocation());
        holder.tvAuthor.setText("• " + model.getAuthorName());

        // 2. Format Date (e.g., "Apr 18")
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(model.getTimestamp())));

        // 3. Load Image (or set placeholder)
        if (model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(model.getImageUrl())
                    .centerCrop()
                    .into(holder.ivImage);
        } else {
            // Set a default icon if no image was uploaded
            holder.ivImage.setImageResource(R.drawable.ic_lost_found); // Use a generic icon
        }

        // 4. *** LOGIC FOR LOST/FOUND TAG ***
        if ("FOUND".equals(model.getStatus())) {
            holder.chipStatus.setText("FOUND");
            holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#4CAF50"))); // Green
        } else {
            holder.chipStatus.setText("LOST");
            holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#D32F2F"))); // Red
        }

        // 5. Click listener to open details page (Phase 3)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                // We pass the Document ID to the details page
                String documentId = getSnapshots().getSnapshot(position).getId();
                listener.onItemClick(documentId);
            }
        });
    }

    @NonNull
    @Override
    public LostFoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lost_found, parent, false);
        return new LostFoundViewHolder(view);
    }

    // --- Interface for click events ---
    public interface OnItemClickListener {
        void onItemClick(String documentId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // --- ViewHolder Class ---
    static class LostFoundViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvLocation, tvDate, tvAuthor;
        Chip chipStatus;

        public LostFoundViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_item_image);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvLocation = itemView.findViewById(R.id.tv_item_location);
            tvDate = itemView.findViewById(R.id.tv_item_date);
            tvAuthor = itemView.findViewById(R.id.tv_item_author);
            chipStatus = itemView.findViewById(R.id.chip_status_tag);
        }
    }
}