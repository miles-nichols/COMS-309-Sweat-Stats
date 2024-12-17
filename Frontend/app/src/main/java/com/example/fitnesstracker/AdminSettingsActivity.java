package com.example.fitnesstracker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminSettingsActivity extends AppCompatActivity implements ReportsAdapter.OnReportActionListener {

    private RecyclerView recyclerView;
    private ReportsAdapter adapter;
    private List<Report> reports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reports = new ArrayList<>();
        adapter = new ReportsAdapter(reports, this);
        recyclerView.setAdapter(adapter);

        fetchReports();
    }

    private void fetchReports() {
        reports.clear();
        adapter.notifyDataSetChanged();
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/report"; // Modify with the correct URL

        Log.d("AdminSettingsActivity", "Making request to fetch reports...");

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("AdminSettingsActivity", "Fetching reports...");
                    Log.d("AdminSettingsActivity", "Response: " + response.toString());

                    try {
                        // Iterate over the reports
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject reportJson = response.getJSONObject(i);

                            // Extract the "reported" object, and get necessary fields
                            JSONObject reported = reportJson.getJSONObject("reported");
                            String username = reported.getString("username");
                            String role = reported.getString("role");
                            int strikes = reported.getInt("strikes");

                            // Extract other report fields
                            String summary = reportJson.getString("summary");
                            String description = reportJson.getString("description");
                            String reportDate = reportJson.getString("reportDate");
                            boolean completed = reportJson.getBoolean("completed");
                            int reportId = reportJson.getInt("reportId");
                            String reportedUsername = reportJson.getString("reported_username");
                            String reporterUsername = reportJson.getString("reporter_username");

                            // Create a Report object with the extracted data
                            Report report = new Report(username, role, strikes, summary, description, reportDate,
                                    completed, reportId, reportedUsername, reporterUsername);

                            // Add the report to the list
                            reports.add(report);
                        }

                        // Notify the adapter that data has changed
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("AdminSettingsActivity", "Error in request: " + error.toString());  // Log error response
                    Toast.makeText(AdminSettingsActivity.this, "Error fetching reports", Toast.LENGTH_SHORT).show();
                });

        // Send the request
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }




    @Override
    public void onAccept(String summary) {
        // Find the report by its summary
        Report report = findReportBySummary(summary);
        if (report != null) {
            String reporterUsername = report.getReporterUsername();
            String reportedUsername = report.getReportedUsername();
            String reportSummary = report.getSummary();

            // Construct the URL for resolving the report
            String url = "http://coms-3090-014.class.las.iastate.edu:8080/report/resolve?reporterUsername="
                    + reporterUsername + "&reportedUsername=" + reportedUsername + "&summary=" + reportSummary;

            // Make the PUT request to resolve the report
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, null,
                    response -> {
                        // Handle success response
                        Toast.makeText(this, "Report accepted", Toast.LENGTH_SHORT).show();
                        fetchReports(); // Refresh the report list
                    },
                    error -> {
                        // Handle error
                        Toast.makeText(this, "Failed to accept report", Toast.LENGTH_SHORT).show();
                    });

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }
        fetchReports();
    }

    @Override
    public void onDismiss(String summary) {
        // Find the report by its summary
        Report report = findReportBySummary(summary);
        if (report != null) {
            String reporterUsername = report.getReporterUsername();
            String reportedUsername = report.getReportedUsername();
            String reportSummary = report.getSummary();

            // Construct the URL for dismissing the report
            String url = "http://coms-3090-014.class.las.iastate.edu:8080/report/dismiss?reporterUsername="
                    + reporterUsername + "&reportedUsername=" + reportedUsername + "&summary=" + reportSummary;

            // Make the PUT request to dismiss the report
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, null,
                    response -> {
                        // Handle success response
                        Toast.makeText(this, "Report dismissed", Toast.LENGTH_SHORT).show();
                        fetchReports(); // Refresh the report list
                    },
                    error -> {
                        // Handle error
                        Toast.makeText(this, "Failed to dismiss report", Toast.LENGTH_SHORT).show();
                    });

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }
        fetchReports();
    }

    // Helper method to find a report by its summary
    private Report findReportBySummary(String summary) {
        for (Report report : reports) {
            if (report.getSummary().equals(summary)) {
                return report;
            }
        }
        return null; // Return null if the report is not found
    }

    public List<Report> getReports() {
        return reports;
    }
}
