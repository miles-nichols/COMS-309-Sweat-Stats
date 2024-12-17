package com.example.fitnesstracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportViewHolder> {

    private final List<Report> reports;
    private final OnReportActionListener listener;

    public interface OnReportActionListener {
        void onAccept(String summary);  // Use summary instead of reportId
        void onDismiss(String summary);  // Use summary instead of reportId
    }

    public ReportsAdapter(List<Report> reports, OnReportActionListener listener) {
        this.reports = reports;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reports.get(position);

        // Bind data to the UI elements
        holder.usernameTextView.setText("Reported User: " + report.getUsername());
        holder.reporterTextView.setText("Reporter: " + report.getReporterUsername());
        holder.summaryTextView.setText("Summary: " + report.getSummary());
        holder.reportDateTextView.setText("Report Date: " + report.getReportDate());
        holder.strikesTextView.setText("Strikes: " + report.getStrikes());

        // Set up accept and dismiss buttons
        holder.acceptButton.setOnClickListener(v -> listener.onAccept(report.getSummary()));  // Pass summary
        holder.dismissButton.setOnClickListener(v -> listener.onDismiss(report.getSummary()));  // Pass summary
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {

        private final TextView usernameTextView;
        private final TextView reporterTextView;
        private final TextView summaryTextView;
        private final TextView reportDateTextView;
        private final TextView strikesTextView;
        private final Button acceptButton;
        private final Button dismissButton;

        public ReportViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            reporterTextView = itemView.findViewById(R.id.reporterTextView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            reportDateTextView = itemView.findViewById(R.id.reportDateTextView);
            strikesTextView = itemView.findViewById(R.id.strikesTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            dismissButton = itemView.findViewById(R.id.dismissButton);
        }
    }
}
