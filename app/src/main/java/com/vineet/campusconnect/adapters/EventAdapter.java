package com.vineet.campusconnect.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.vineet.campusconnect.R;
import com.vineet.campusconnect.models.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.tvTitle.setText(event.getTitle());
        holder.tvDate.setText(event.getDate());
        holder.tvLocation.setText(event.getLocation() + " • " + event.getTime());
        holder.chipStatus.setText(event.getType());

        if (event.getType().equals("ONGOING")) {
            holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#4CAF50"))); // Green
        } else {
            holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#6200EE"))); // Purple
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvLocation;
        Chip chipStatus;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_event_title);
            tvDate = itemView.findViewById(R.id.tv_event_date);
            tvLocation = itemView.findViewById(R.id.tv_event_location);
            chipStatus = itemView.findViewById(R.id.chip_event_status);
        }
    }
}