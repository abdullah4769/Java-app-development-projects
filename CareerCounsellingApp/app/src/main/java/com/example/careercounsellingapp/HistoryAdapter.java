package com.example.careercounsellingapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.careercounsellingapp.data.entities.UserResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<UserResult> results;

    public HistoryAdapter(List<UserResult> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserResult result = results.get(position);
        
        // 1. Correctly binding the User Name from the database
        holder.tvUserName.setText(result.getUserName() != null ? result.getUserName() : "Guest User");
        
        holder.tvName.setText(result.getTopCareerPath());
        
        int matchPercent = (int) result.getAnalyticalScore();
        holder.tvPercent.setText(matchPercent + "% Match");

        String type = result.getAssessmentType();
        holder.tvBadge.setText(type != null ? type : "Standard Assessment");
        
        if (type != null && type.contains("Situational")) {
            holder.tvBadge.setBackgroundColor(Color.parseColor("#3364B5F6")); // Soft Blue
            holder.tvBadge.setTextColor(Color.parseColor("#64B5F6"));
        } else {
            holder.tvBadge.setBackgroundColor(Color.parseColor("#33FFD700")); // Soft Gold
            holder.tvBadge.setTextColor(Color.parseColor("#FFD700"));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        String dateStr = sdf.format(new Date(result.getTimestamp()));
        holder.tvDate.setText("Assessment on " + dateStr);
    }

    @Override
    public int getItemCount() {
        return results != null ? results.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvName, tvPercent, tvDate, tvBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 2. Updated IDs to match item_history.xml
            tvUserName = itemView.findViewById(R.id.tvHistoryUserName);
            tvName = itemView.findViewById(R.id.tvHistoryName);
            tvPercent = itemView.findViewById(R.id.tvHistoryPercentage);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvBadge = itemView.findViewById(R.id.tvAssessmentBadge);
        }
    }
}