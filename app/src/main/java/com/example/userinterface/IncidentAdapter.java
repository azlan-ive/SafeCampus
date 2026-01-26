package com.example.userinterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

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
        holder.tvType.setText("Type: " + item.type);
        holder.tvDesc.setText(item.description);
        holder.tvTime.setText(item.time);
    }

    @Override
    public int getItemCount() {
        return incidents.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvDesc, tvTime;

        ViewHolder(View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}