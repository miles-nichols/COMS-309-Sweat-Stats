package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.util.Log; // For Log statements

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvStreaks, tvFavoriteLiftProgress, tvMuscleGroupProgress, tvRecentWorkout;
    private ImageButton btnFriends, btnLiftSearch, btnCalendar, btnSettings;
    private RequestQueue requestQueue;
    private final String BASE_URL = "http://coms-3090-014.class.las.iastate.edu:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize UI Components
        tvStreaks = findViewById(R.id.tvStreaks);
        tvFavoriteLiftProgress = findViewById(R.id.tvFavoriteLiftProgress);
        tvMuscleGroupProgress = findViewById(R.id.tvMuscleGroupProgress);
        tvRecentWorkout = findViewById(R.id.tvRecentWorkout);
        btnFriends = findViewById(R.id.btnFriends);
        btnLiftSearch = findViewById(R.id.btnLiftSearch);
        btnCalendar = findViewById(R.id.btnCalendar);
        btnSettings = findViewById(R.id.btnSettings);
        requestQueue = Volley.newRequestQueue(this);

        // Get the currently logged-in username
        String username = ActivityLogin.getUsername();
        if (username != null) {
            fetchStreak(username);
            fetchProgress(username, "Bench Press", "2024-11-01", "2024-11-15", "2024-12-01", "2024-12-15");
            fetchMuscleGroupProgress(username, "Chest", "2024-11-01", "2024-11-15", "2024-12-01", "2024-12-15");
            fetchLastWorkout(username);
        } else {
            Toast.makeText(this, "Error: Unable to fetch user data. Please log in again.", Toast.LENGTH_SHORT).show();
        }

        btnFriends.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ActivityFriends.class);
            startActivity(intent);
        });

        btnLiftSearch.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ActivityLiftSearch.class);
            startActivity(intent);
        });

        btnCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
        });

    }

    private void fetchStreak(String username) {
        String url = BASE_URL + "/user/streak/" + username;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> tvStreaks.setText("Workout Streak: " + response.trim() + " days"),
                error -> Toast.makeText(this, "Error fetching streak", Toast.LENGTH_SHORT).show());
        requestQueue.add(request);
    }

    private void fetchProgress(String username, String liftName, String tf1s, String tf1e, String tf2s, String tf2e) {
        if (username == null || username.isEmpty()) {
            Log.e("DashboardActivity", "Invalid username for lift progress.");
            return;
        }

        String url = BASE_URL + "/user/progress/" + username + "/lift/" + liftName +
                "?tf1s=" + tf1s + "&tf1e=" + tf1e + "&tf2s=" + tf2s + "&tf2e=" + tf2e;

        Log.d("DashboardActivity", "Fetching lift progress from URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    String progressString = response.optString("comparison", "0");
                    try {
                        // Parse progress as a double and round to the nearest hundredth
                        double progress = Double.parseDouble(progressString);
                        String formattedProgress = String.format("%.2f", progress);

                        // Update the TextView with the personalized message
                        tvFavoriteLiftProgress.setText("You have improved " + liftName + " by " + formattedProgress + "%");
                        Log.d("DashboardActivity", "Lift progress fetched successfully: " + formattedProgress);
                    } catch (NumberFormatException e) {
                        Log.e("DashboardActivity", "Error parsing progress value: " + progressString, e);
                        tvFavoriteLiftProgress.setText("You have improved " + liftName + " by N/A%");
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        String errorData = new String(error.networkResponse.data);
                        Log.e("DashboardActivity", "Error response from server: " + errorData);
                    }
                    String errorMessage = error.networkResponse != null
                            ? "Server Error: " + error.networkResponse.statusCode
                            : "Network Error";
                    Log.e("DashboardActivity", "Error fetching lift progress: " + errorMessage, error);
                    Toast.makeText(this, "Failed to fetch lift progress.", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }




    private void fetchLastWorkout(String username) {
        String url = BASE_URL + "/user/lastWorkout/" + username;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    String workoutName = response.optString("name", "N/A");
                    String workoutDate = response.optString("date", "N/A");
                    JSONArray completedSets = response.optJSONArray("completedSets");

                    StringBuilder workoutDetails = new StringBuilder("Last Workout: " + workoutName + " on " + workoutDate + "\nDetails:\n");

                    if (completedSets != null) {
                        for (int i = 0; i < completedSets.length(); i++) {
                            JSONObject set = completedSets.optJSONObject(i);
                            if (set != null) {
                                JSONObject lift = set.optJSONObject("lift");
                                if (lift != null) {
                                    String liftTitle = lift.optString("title", "Unknown Lift");
                                    workoutDetails.append(liftTitle).append(":\n");

                                    JSONArray sets = set.optJSONArray("sets");
                                    if (sets != null) {
                                        for (int j = 0; j < sets.length(); j++) {
                                            JSONObject setDetail = sets.optJSONObject(j);
                                            if (setDetail != null) {
                                                int reps = setDetail.optInt("reps", 0);
                                                int weight = setDetail.optInt("weight", 0);
                                                workoutDetails.append("  ").append(reps).append(" reps @ ").append(weight).append(" lbs\n");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    tvRecentWorkout.setText(workoutDetails.toString());
                },
                error -> Toast.makeText(this, "Error fetching last workout", Toast.LENGTH_SHORT).show());
        requestQueue.add(request);
    }

    private void fetchMuscleGroupProgress(String username, String muscleGroup, String tf1s, String tf1e, String tf2s, String tf2e) {
        if (username == null || username.isEmpty()) {
            Log.e("DashboardActivity", "Invalid username for muscle group progress.");
            return;
        }

        String url = BASE_URL + "/user/progress/" + username + "/muscleGroup/" + muscleGroup +
                "?tf1s=" + tf1s + "&tf1e=" + tf1e + "&tf2s=" + tf2s + "&tf2e=" + tf2e;

        Log.d("DashboardActivity", "Fetching muscle group progress from URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    String progress = response.optString("comparison", "N/A");
                    tvMuscleGroupProgress.setText("Muscle Group Progress: " + progress + "%");
                    Log.d("DashboardActivity", "Muscle group progress fetched successfully: " + progress);
                },
                error -> {
                    if (error.networkResponse != null) {
                        String errorData = new String(error.networkResponse.data);
                        Log.e("DashboardActivity", "Error response from server: " + errorData);
                    }
                    String errorMessage = error.networkResponse != null
                            ? "Server Error: " + error.networkResponse.statusCode
                            : "Network Error";
                    Log.e("DashboardActivity", "Error fetching muscle group progress: " + errorMessage, error);
                    Toast.makeText(this, "Failed to fetch muscle group progress.", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

}
