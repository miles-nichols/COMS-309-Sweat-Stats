package com.example.fitnesstracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityNewWorkout extends AppCompatActivity {
    private static final int LIFT_SEARCH_REQUEST_CODE = 100;
    private static final String TAG = "ActivityNewWorkout";

    private EditText etWorkoutName;
    private LinearLayout liftsContainer;
    private Button btnAddLiftBelow;
    private Button btnSaveWorkout;

    private ArrayList<ObjectLift> liftList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_workout);

        etWorkoutName = findViewById(R.id.etWorkoutName);
        liftsContainer = findViewById(R.id.liftsContainer);
        btnAddLiftBelow = findViewById(R.id.btnAddLiftBelow);
        btnSaveWorkout = findViewById(R.id.btnSaveWorkout);

        liftList = new ArrayList<>();

        btnAddLiftBelow.setOnClickListener(v -> navigateToLiftSearch());
        btnSaveWorkout.setOnClickListener(v -> saveWorkout());
    }

    private void navigateToLiftSearch() {
        Intent intent = new Intent(this, ActivityLiftSearch.class);
        startActivityForResult(intent, LIFT_SEARCH_REQUEST_CODE);
    }

    private void addLiftView(ObjectLift lift) {
        // Display lift title for simplicity
        TextView tvLift = new TextView(this);
        tvLift.setText(lift.getTitle());
        tvLift.setTextSize(18f);
        tvLift.setPadding(8, 8, 8, 8);
        liftsContainer.addView(tvLift, liftsContainer.getChildCount() - 1); // Add above the plus button
    }

    private void saveWorkout() {
        String workoutName = etWorkoutName.getText().toString().trim();
        if (workoutName.isEmpty()) {
            Toast.makeText(this, "Please enter a workout name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (liftList.isEmpty()) {
            Toast.makeText(this, "Please add at least one lift.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Step 1: Create the workout without lifts
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", workoutName);
            requestBody.put("description", ""); // You can allow users to set a description if needed
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating workout data", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://coms-3090-014.class.las.iastate.edu:8080/workout";

        JsonObjectRequest createWorkoutRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    Log.d(TAG, "Workout created: " + workoutName);
                    // Step 2: Associate each lift with the newly created workout
                    addLiftsToWorkout(workoutName);
                },
                error -> {
                    Log.e(TAG, "Error creating workout: " + error.toString());
                    Toast.makeText(this, "Error creating workout", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(createWorkoutRequest);
    }

    private void addLiftsToWorkout(String workoutName) {
        if (liftList.isEmpty()) {
            // No lifts to add, just end here
            Toast.makeText(this, "Workout saved successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ActivityHomeScreen.class);
            startActivity(intent);
            finish();
            return;
        }

        final int[] completedRequests = {0};
        for (ObjectLift lift : liftList) {
            String url = "http://coms-3090-014.class.las.iastate.edu:8080/workout/"
                    + workoutName + "?liftName=" + Uri.encode(lift.getTitle());

            Log.d(TAG, "Associating lift with Name: " + lift.getTitle() + " to workout: " + workoutName);

            JsonObjectRequest addLiftRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    null,
                    response -> {
                        Log.d(TAG, "Successfully added lift: " + lift.getTitle());
                        completedRequests[0]++;
                        if (completedRequests[0] == liftList.size()) {
                            Toast.makeText(this, "Workout saved successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, ActivityHomeScreen.class);
                            startActivity(intent);
                            finish();
                        }
                    },
                    error -> {
                        Log.e(TAG, "Error adding lift: " + lift.getTitle() + " - " + error.toString());
                        completedRequests[0]++;
                        Toast.makeText(this, "Error adding lift: " + lift.getTitle(), Toast.LENGTH_SHORT).show();
                        if (completedRequests[0] == liftList.size()) {
                            Toast.makeText(this, "Workout saved with some errors!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, ActivityHomeScreen.class);
                            startActivity(intent);
                            finish();
                        }
                    }
            );

            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(addLiftRequest);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LIFT_SEARCH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            HashMap<String, String> selectedLift = (HashMap<String, String>) data.getSerializableExtra("selectedLift");
            if (selectedLift != null) {
                String title = selectedLift.get("title");
                String description = selectedLift.get("description");
                String type = selectedLift.get("type");
                String muscleGroup = selectedLift.get("muscleGroup");
                String equipment = selectedLift.get("equipment");
                String level = selectedLift.get("level");

                ObjectLift newLift = new ObjectLift(title, description, type, muscleGroup, equipment, level);
                liftList.add(newLift);
                addLiftView(newLift);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
