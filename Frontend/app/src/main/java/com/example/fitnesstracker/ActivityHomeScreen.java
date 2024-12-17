package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityHomeScreen extends AppCompatActivity {

    // Buttons for original activities
    private Button btnNewWorkout, btnCreateWorkout;
    private ImageButton btnProfile, btnFriends, btnLiftSearch, btnCalendar, btnSettings;

    // Dashboard components
    private TextView tvStreaks, tvFavoriteLiftProgress, tvMuscleGroupProgress, tvRecentWorkout;

    private RequestQueue requestQueue;
    private final String BASE_URL = "http://coms-3090-014.class.las.iastate.edu:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI Components
        btnNewWorkout = findViewById(R.id.btnNewWorkout);
        btnProfile = findViewById(R.id.btnProfile);
        btnFriends = findViewById(R.id.btnFriends);
        btnLiftSearch = findViewById(R.id.btnLiftSearch);
        btnCalendar = findViewById(R.id.btnCalendar);
        btnSettings = findViewById(R.id.btnSettings);

        // Dashboard TextViews
        tvStreaks = findViewById(R.id.tvStreaks);
        tvFavoriteLiftProgress = findViewById(R.id.tvFavoriteLiftProgress);
        tvMuscleGroupProgress = findViewById(R.id.tvMuscleGroupProgress);
        tvRecentWorkout = findViewById(R.id.tvRecentWorkout);

        requestQueue = Volley.newRequestQueue(this);

        btnNewWorkout.setOnClickListener(v -> startActivity(new Intent(this, ActivityStartWorkout.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        btnFriends.setOnClickListener(v -> startActivity(new Intent(this, SocialHubActivity.class)));
        btnLiftSearch.setOnClickListener(v -> startActivity(new Intent(this, ActivityMyWorkouts.class)));
        btnCalendar.setOnClickListener(v -> startActivity(new Intent(this, CalendarActivity.class)));
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityLogin.getUserLevel().equals("ADMIN")) {
                    startActivity(new Intent(ActivityHomeScreen.this, AdminSettingsActivity.class));
                } else {
                    startActivity(new Intent(ActivityHomeScreen.this, SettingsActivity.class));
                }
            }
        });

        String username = ActivityLogin.getUsername();

        if (username != null) {
            fetchProfilePicture(username);
        } else {
            Toast.makeText(this, "Error: Unable to fetch user data. Please log in again.", Toast.LENGTH_SHORT).show();
        }

        if (username != null) {
            fetchStreak(username);
            String tf2s = "2024-12-04";
            String tf2e = "2024-12-12";
            String tf1s = "2024-11-15";
            String tf1e = "2024-12-05";
            fetchProgress(username, tf1s, tf1e, tf2s, tf2e);

            // Validate date ranges before fetching muscle group progress


            if (isDateRangeValid(tf1s, tf1e) && isDateRangeValid(tf2s, tf2e)) {
                fetchMuscleGroupProgress(username, tf1s, tf1e, tf2s, tf2e);
            } else {
                Log.e("ActivityHomeScreen", "Invalid date range");
                Toast.makeText(this, "Please provide a valid date range.", Toast.LENGTH_SHORT).show();
            }

            fetchLastWorkout(username);
        } else {
            Toast.makeText(this, "Error: Unable to fetch user data. Please log in again.", Toast.LENGTH_SHORT).show();
        }
    }


    private void fetchStreak(String username) {
        String url = BASE_URL + "/user/streak/" + username;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> tvStreaks.setText("Workout Streak: " + response.trim() + " days"),
                error -> Toast.makeText(this, "Error fetching streak", Toast.LENGTH_SHORT).show());
        requestQueue.add(request);
    }

    private void fetchProfilePicture(String username) {
        String pictureUrl = BASE_URL + "/user/picture/" + username;

        // Use Glide or Picasso for image loading
        Glide.with(this)
                .load(pictureUrl)
                .placeholder(R.drawable.ic_profile) // Placeholder while loading
                .error(R.drawable.ic_profile)       // Fallback if there's an error
                .circleCrop()                       // Ensure the image is circular
                .into(btnProfile);                  // Set the image into the ImageButton
    }

    private void fetchProgress(String username, String tf1s, String tf1e, String tf2s, String tf2e) {
        String url = BASE_URL + "/user/progress/random/lift/" + username +
                "?tf1s=" + tf1s + "&tf1e=" + tf1e + "&tf2s=" + tf2s + "&tf2e=" + tf2e;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    String progressString = response.optString("comparison", "0");
                    try {
                        double progress = Double.parseDouble(progressString);
                        String formattedProgress = String.format("%.2f", progress);
                        tvFavoriteLiftProgress.setText("You have improved " + response.get("liftName") + " by " + formattedProgress + "%");
                    } catch (NumberFormatException e) {
                        try {
                            tvFavoriteLiftProgress.setText("You have improved " + response.get("liftName") + " by N/A%");
                        } catch (JSONException ex) {
                            throw new RuntimeException(ex);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Toast.makeText(this, "Failed to fetch progress.", Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }

    private void fetchMuscleGroupProgress(String username, String tf1s, String tf1e, String tf2s, String tf2e) {
        String url = BASE_URL + "/user/progress/random/muscleGroup/" + username +
                "?tf1s=" + tf1s + "&tf1e=" + tf1e + "&tf2s=" + tf2s + "&tf2e=" + tf2e;

        Log.d("ActivityHomeScreen", "Fetching muscle group progress from URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    String formattedProgress = null;
                    try {
                        formattedProgress = String.format("%.2f", response.get("comparison"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        tvMuscleGroupProgress.setText("You have improved " + response.get("muscleGroup") + " by " + formattedProgress + "%");
                    } catch (JSONException e) {
                        Log.e("fetchMuscleGroupProgress", "Error parsing response", e);
                        tvMuscleGroupProgress.setText("Unable to retrieve muscle group progress.");
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        String errorData = new String(error.networkResponse.data);
                        Log.e("ActivityHomeScreen", "Error response from server: " + errorData);
                    }
                    String errorMessage = error.networkResponse != null
                            ? "Server Error: " + error.networkResponse.statusCode
                            : "Network Error";
                    Log.e("ActivityHomeScreen", "Error fetching max: " + errorMessage, error);
                    Toast.makeText(this, "Failed to fetch max.", Toast.LENGTH_SHORT).show();
                });

        // Set a custom RetryPolicy for longer timeout
        int socketTimeout = 30000; // 30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(
                socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Retry count
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Backoff multiplier
        );
        request.setRetryPolicy(policy);

        requestQueue.add(request);
    }

//    private void fetchMuscleGroupProgress(String username) {
//        String url = BASE_URL + "/user/max/random/" + username;
//
//        Log.d("ActivityHomeScreen", "Fetching muscle group progress from URL: " + url);
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
//                response -> {
//                    String progressString = response.optString("comparison", "N/A");
//                    try {
//                        tvMuscleGroupProgress.setText("for " + response.get("liftName") + " is " + response.get("maxWeight"));
//                    } catch (NumberFormatException e) {
//                        try {
//                            tvMuscleGroupProgress.setText("Your current max for " + response.get("liftName") + " is " + response.get("maxWeight"));
//                        } catch (JSONException ex) {
//                            throw new RuntimeException(ex);
//                        }
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                },
//                error -> {
//                    if (error.networkResponse != null) {
//                        String errorData = new String(error.networkResponse.data);
//                        Log.e("ActivityHomeScreen", "Error response from server: " + errorData);
//                    }
//                    String errorMessage = error.networkResponse != null
//                            ? "Server Error: " + error.networkResponse.statusCode
//                            : "Network Error";
//                    Log.e("ActivityHomeScreen", "Error fetching max : " + errorMessage, error);
//                    Toast.makeText(this, "Failed to fetch max.", Toast.LENGTH_SHORT).show();
//                });
//
//        requestQueue.add(request);
//    }


    private boolean isDateRangeValid(String start, String end) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);
            return startDate != null && endDate != null && startDate.before(endDate);
        } catch (ParseException e) {
            Log.e("DateValidation", "Invalid date format", e);
            return false;
        }
    }



    private void fetchLastWorkout(String username) {
        String url = BASE_URL + "/user/lastWorkout/" + username;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    String completedWorkoutName = response.optString("name", "N/A");
                    String originalWorkoutName = extractOriginalWorkoutName(completedWorkoutName);
                    String workoutDate = response.optString("date", "N/A");
                    tvRecentWorkout.setText("Last Workout: " + originalWorkoutName + " on " + workoutDate);
                },
                error -> {
                    if (error.networkResponse != null) {
                        String errorData = new String(error.networkResponse.data);
                        Log.e("ActivityHomeScreen", "Error response from server: " + errorData);
                    }
                    String errorMessage = error.networkResponse != null
                            ? "Server Error: " + error.networkResponse.statusCode
                            : "Network Error";
                    Log.e("ActivityHomeScreen", "Error fetching last workout: " + errorMessage, error);
                    Toast.makeText(this, "Error fetching last workout", Toast.LENGTH_SHORT).show();
                });
        requestQueue.add(request);
    }

    /**
     * Extracts the original workout name by removing the "_completed_<timestamp>" suffix.
     *
     * @param completedWorkoutName The name of the completed workout (e.g., "ass_completed_20241209200620").
     * @return The original workout name (e.g., "ass").
     */
    private String extractOriginalWorkoutName(String completedWorkoutName) {
        // Assuming the format is "workoutName_completed_timestamp"
        String[] parts = completedWorkoutName.split("_completed_");
        if (parts.length > 1) {
            return parts[0];
        }
        return completedWorkoutName; // Return as is if the pattern doesn't match
    }

}
