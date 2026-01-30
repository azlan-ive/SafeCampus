package com.example.userinterface;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.ViewHolder> {

    private List<IncidentUI> incidents;

    public IncidentAdapter(List<IncidentUI> incidents) {
        this.incidents = incidents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.incidentcard, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IncidentUI item = incidents.get(position);
        holder.tvType.setText(item.type);
        holder.tvDesc.setText(item.description);
        holder.tvTime.setText(item.time);
        
        // Display Reporter Name
        if (item.username != null && !item.username.isEmpty()) {
            holder.tvBy.setText("by " + item.username);
        } else {
            holder.tvBy.setText("by Anonymous");
        }

        // Display User ID
        if (item.userId != null && !item.userId.isEmpty()) {
            holder.tvUserId.setText("User ID: " + item.userId);
        } else {
            holder.tvUserId.setText("User ID: Anonymous");
        }

        if (item.latitude != 0 && item.longitude != 0) {
            holder.tvLocation.setText(String.format(Locale.getDefault(), "%.4f, %.4f", item.latitude, item.longitude));
        } else {
            holder.tvLocation.setText("No location recorded");
        }

        // Click card to view location on Google Maps
        holder.itemView.setOnClickListener(v -> {
            if (item.latitude != 0 && item.longitude != 0) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)", 
                        item.latitude, item.longitude, item.latitude, item.longitude, item.type);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return incidents.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvDesc, tvTime, tvLocation, tvUserId, tvBy;

        ViewHolder(View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvBy = itemView.findViewById(R.id.tvBy);
        }
    }
}
