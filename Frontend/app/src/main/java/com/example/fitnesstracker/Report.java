package com.example.fitnesstracker;

import java.io.Serializable;

public class Report {
    private String username;
    private String role;
    private int strikes;
    private String summary;
    private String description;
    private String reportDate;
    private boolean completed;
    private int reportId;
    private String reportedUsername;
    private String reporterUsername;

    // Constructor to initialize the report
    public Report(String username, String role, int strikes, String summary, String description,
                  String reportDate, boolean completed, int reportId,
                  String reportedUsername, String reporterUsername) {
        this.username = username;
        this.role = role;
        this.strikes = strikes;
        this.summary = summary;
        this.description = description;
        this.reportDate = reportDate;
        this.completed = completed;
        this.reportId = reportId;
        this.reportedUsername = reportedUsername;
        this.reporterUsername = reporterUsername;
    }

    // Getter methods
    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getReportDate() {
        return reportDate;
    }

    public int getReportId() {
        return reportId;
    }

    public String getReportedUsername() {
        return reportedUsername;
    }

    public String getReporterUsername() {
        return reporterUsername;
    }

    // Getter and Setter for strikes
    public int getStrikes() {
        return strikes;
    }

    public void setStrikes(int strikes) {
        this.strikes = strikes;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
