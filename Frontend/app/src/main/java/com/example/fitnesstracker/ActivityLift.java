// ActivityLift.java

package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity to handle the lifting session, allowing users to complete their workout
 * and send the completed workout data to the backend.
 */
public class ActivityLift extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LiftPagerAdapter liftPagerAdapter;
    private List<ObjectLift> lifts;
    private ObjectWorkout objectWorkout;
    private ObjectActiveWorkout activeWorkout;
    private Button btnCompleteWorkout; // Button to complete workout
    private String selectedDate;
    private boolean isPastWorkout;

    private boolean isWorkoutCreated = false;
    private String completedWorkoutName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lift);

        // Retrieve workout data from intent
        objectWorkout = (ObjectWorkout) getIntent().getSerializableExtra("workout");
        activeWorkout = (ObjectActiveWorkout) getIntent().getSerializableExtra("activeWorkout");
        selectedDate = getIntent().getStringExtra("selectedDate");
        isPastWorkout = getIntent().getBooleanExtra("isPastWorkout", isPastWorkout);


        if (objectWorkout == null || activeWorkout == null) {
            Toast.makeText(this, "Workout or active workout data is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        lifts = objectWorkout.getLifts();
        if (lifts == null) {
            lifts = new ArrayList<>();
        }

        // Setup Complete Workout button
        btnCompleteWorkout = findViewById(R.id.btnCompleteWorkout);
        btnCompleteWorkout.setOnClickListener(v -> {
            // Navigate back to ActivityHomeScreen
            Intent intent = new Intent(ActivityLift.this, ActivityHomeScreen.class);
            startActivity(intent);
            finish(); // Optional: Finish current activity to prevent going back
        });

        // Automatically create CompletedWorkout and LiftSets
        createCompletedWorkout();
    }

    /**
     * Automatically creates a CompletedWorkout and corresponding LiftSets by sending a POST request.
     */
    private void createCompletedWorkout() {
        if (isWorkoutCreated) {
            Log.d("ActivityLift", "CompletedWorkout already created.");
            return;
        }

        String workoutName = objectWorkout.getName(); // Workout Template Name

        // Ensure workoutName is not null or empty
        if (workoutName == null || workoutName.trim().isEmpty()) {
            Toast.makeText(this, "Invalid workout name.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique completed workout name by appending a timestamp
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
        completedWorkoutName = workoutName + "_completed_" + timestamp; // e.g., benchbench_completed_20241209213213

        String username = ActivityLogin.getUsername(); // Get username

        // Ensure username is not null or empty
        if (username == null || username.trim().isEmpty()) {
            Toast.makeText(this, "Invalid username.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log the parameters for debugging
        Log.d("ActivityLift", "Creating CompletedWorkout with parameters:");
        Log.d("ActivityLift", "Completed Workout Name: " + completedWorkoutName);
        Log.d("ActivityLift", "Workout Template Name: " + workoutName);
        Log.d("ActivityLift", "Username: " + username);

        try {
            // Encode URL parameters to handle special characters
            String encodedName = URLEncoder.encode(completedWorkoutName, "UTF-8");
            String encodedWorkoutName = URLEncoder.encode(workoutName, "UTF-8");
            String encodedUsername = URLEncoder.encode(username, "UTF-8");

            String url = "";
            if (isPastWorkout) {
                url = "http://coms-3090-014.class.las.iastate.edu:8080/completedWorkout/" + encodedName + "/workout/" + encodedWorkoutName + "/" + selectedDate + "?username=" + encodedUsername;
            }
            else{
                url = "http://coms-3090-014.class.las.iastate.edu:8080/completedWorkout/" + encodedName + "/workout/" + encodedWorkoutName + "?username=" + encodedUsername;


            }
            Log.d("ActivityLift", "POST URL: " + url);

            // Construct the JSON request body with liftSets populated from objectWorkout.getLifts()
            JSONObject requestBody = new JSONObject();
            requestBody.put("name", completedWorkoutName);

            // Add liftSets to the request body with empty sets
            JSONArray liftsArray = new JSONArray();
            for (ObjectLift lift : objectWorkout.getLifts()) {
                Log.d("ActivityLift", "Processing Lift: " + lift.getTitle());

                JSONObject liftObject = new JSONObject();
                liftObject.put("name", lift.getTitle());

                JSONArray setsArray = new JSONArray(); // Empty sets
                liftObject.put("sets", setsArray);
                liftsArray.put(liftObject);
            }
            requestBody.put("liftSets", liftsArray);

            Log.d("ActivityLift", "Request Body: " + requestBody.toString());

            // Create the POST request
            JsonObjectRequest postRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    requestBody, // Sending the populated JSON object
                    response -> {
                        // Handle successful completion
                        Toast.makeText(this, "Workout created successfully!", Toast.LENGTH_SHORT).show();
                        Log.d("ActivityLift", "Workout created: " + response.toString());
                        isWorkoutCreated = true;

                        // Now initialize the ViewPager with LiftFragments
                        initializeViewPager();
                    },
                    error -> {
                        // Handle errors
                        String errorMsg = "Error creating workout.";
                        if (error.networkResponse != null) {
                            errorMsg += " Code: " + error.networkResponse.statusCode;
                            // Attempt to extract detailed message from server response
                            try {
                                String responseData = new String(error.networkResponse.data, "UTF-8");
                                JSONObject responseObj = new JSONObject(responseData);
                                if (responseObj.has("message")) {
                                    errorMsg += " Message: " + responseObj.getString("message");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            errorMsg += " Please check your internet connection.";
                        }
                        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e("ActivityLift", "Error creating workout: ", error);
                        if (error.networkResponse != null) {
                            Log.e("Volley Error", "Response Code: " + error.networkResponse.statusCode);
                            Log.e("Volley Error", "Response Data: " + new String(error.networkResponse.data));
                        }
                    });

            // Add the request to the Volley request queue
            Volley.newRequestQueue(this).add(postRequest);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error encoding URL parameters", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Initializes the ViewPager2 with LiftFragments after the CompletedWorkout is created.
     */
    private void initializeViewPager() {
        if (completedWorkoutName == null) {
            Log.e("ActivityLift", "Cannot initialize ViewPager: completedWorkoutName is null.");
            return;
        }

        viewPager = findViewById(R.id.viewPager);
        liftPagerAdapter = new LiftPagerAdapter(this, lifts, activeWorkout, completedWorkoutName);
        viewPager.setAdapter(liftPagerAdapter);
    }
}
