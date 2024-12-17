package com.example.fitnesstracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

public class ActivityCommunityDetail extends AppCompatActivity {

    private TextView tvCommunityTitle, tvRecommendedLift;
    private Button btnAction;

    private String communityTitle, trainerUsername, currentUserRole, currentUserUsername;
    private String recommendedWorkout = "None";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_details);

        tvCommunityTitle = findViewById(R.id.tvCommunityTitle);
        tvRecommendedLift = findViewById(R.id.tvRecommendedLift);
        btnAction = findViewById(R.id.btnAction);

        // Retrieve intent extras
        communityTitle = getIntent().getStringExtra("communityTitle");
        trainerUsername = getIntent().getStringExtra("trainerUsername");
        currentUserRole = getIntent().getStringExtra("currentUserRole");
        currentUserUsername = getIntent().getStringExtra("currentUserUsername");

        tvCommunityTitle.setText(communityTitle);

        if ("TRAINER".equalsIgnoreCase(currentUserRole)) {
            btnAction.setText("Recommend Workout");
            btnAction.setOnClickListener(v -> showRecommendWorkoutDialog());
        } else {
            btnAction.setText("Start Workout");
            btnAction.setOnClickListener(v -> startRecommendedWorkout());
        }

        fetchRecommendedWorkout();
    }

    private void fetchRecommendedWorkout() {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/trainingCommunity/" + communityTitle + "/recommendedWorkout";

        // Volley request to fetch the recommended workout
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    recommendedWorkout = response.trim();
                    tvRecommendedLift.setText("Recommended Workout: " + recommendedWorkout);
                },
                error -> {
                    Toast.makeText(this, "Error fetching recommended workout", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void showRecommendWorkoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recommend Workout");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_recommend_workout, null);
        builder.setView(dialogView);

        EditText etWorkoutName = dialogView.findViewById(R.id.etWorkoutName);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String workoutName = etWorkoutName.getText().toString().trim();
            if (!workoutName.isEmpty()) {
                recommendWorkout(workoutName);
            } else {
                Toast.makeText(this, "Workout name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void recommendWorkout(String workoutName) {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/trainingCommunity/" + communityTitle + "/recommendWorkout";

        JSONObject payload = new JSONObject();
        try {
            payload.put("workoutName", workoutName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, payload,
                response -> {
                    Toast.makeText(this, "Workout recommended successfully!", Toast.LENGTH_SHORT).show();
                    recommendedWorkout = workoutName;
                    tvRecommendedLift.setText("Recommended Workout: " + workoutName);
                },
                error -> Toast.makeText(this, "Error recommending workout", Toast.LENGTH_SHORT).show());

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void startRecommendedWorkout() {
        if ("None".equalsIgnoreCase(recommendedWorkout)) {
            Toast.makeText(this, "No workout recommended yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ActivityLift.class);
        intent.putExtra("workoutName", recommendedWorkout);
        startActivity(intent);
    }
}
