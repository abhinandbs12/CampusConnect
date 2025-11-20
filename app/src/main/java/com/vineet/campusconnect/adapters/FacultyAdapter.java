package com.vineet.campusconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.vineet.campusconnect.R;
import com.vineet.campusconnect.models.Faculty;

import java.util.ArrayList;
import java.util.List;

// UPDATED: Extends standard RecyclerView.Adapter now
public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.FacultyViewHolder> {

    private Context context;
    private List<Faculty> facultyList;
    private List<Faculty> facultyListFull; // For search filtering
    private OnItemClickListener listener;

    public FacultyAdapter(Context context, List<Faculty> facultyList) {
        this.context = context;
        this.facultyList = facultyList;
        this.facultyListFull = new ArrayList<>(facultyList); // Copy for search
    }

    @NonNull
    @Override
    public FacultyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faculty, parent, false);
        return new FacultyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacultyViewHolder holder, int position) {
        Faculty model = facultyList.get(position);

        holder.tvName.setText(model.getName());
        holder.tvDesignation.setText(model.getDesignation());

        if (model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
            Glide.with(context).load(model.getImageUrl()).into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.ic_nav_profile);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return facultyList.size();
    }

    // --- Search Filter Logic ---
    public void filter(String text) {
        facultyList.clear();
        if (text.isEmpty()) {
            facultyList.addAll(facultyListFull);
        } else {
            text = text.toLowerCase();
            for (Faculty item : facultyListFull) {
                if (item.getName().toLowerCase().contains(text)) {
                    facultyList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    // --- Interface ---
    public interface OnItemClickListener {
        void onItemClick(Faculty faculty);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // --- ViewHolder ---
    static class FacultyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesignation;
        ImageView ivImage;
        public FacultyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_faculty_name);
            tvDesignation = itemView.findViewById(R.id.tv_faculty_designation);
            ivImage = itemView.findViewById(R.id.iv_faculty_image);
        }
    }
}