package com.example.fitnesstracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Activity that displays all saved workouts and allows users to add or delete workouts.
 */
public class ActivityMyWorkouts extends AppCompatActivity {

    private static final String TAG = "ActivityMyWorkouts";
    private static final String WORKOUTS_URL = "http://coms-3090-014.class.las.iastate.edu:8080/workouts";
    private static final String DELETE_WORKOUT_URL = "http://coms-3090-014.class.las.iastate.edu:8080/workout/";
    // DELETE /workout/{name}

    private RecyclerView recyclerView;
    private AdapterWorkout workoutAdapter;
    private ArrayList<ObjectWorkout> workoutList;
    private FloatingActionButton addWorkoutButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_workouts);

        recyclerView = findViewById(R.id.recyclerViewWorkouts);
        addWorkoutButton = findViewById(R.id.btnAddWorkout);
        backButton = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        workoutList = new ArrayList<>();
        // Pass delete callback to adapter
        workoutAdapter = new AdapterWorkout(this, workoutList, this::openWorkoutDetails, this::confirmDeleteWorkout);
        recyclerView.setAdapter(workoutAdapter);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityHomeScreen.class);
            startActivity(intent);
            finish();
        });
        addWorkoutButton.setOnClickListener(v -> navigateToNewWorkout());

        fetchWorkouts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh workouts in case new ones were added
        fetchWorkouts();
    }

    /**
     * Fetches all workouts from the backend server.
     */
    private void fetchWorkouts() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                WORKOUTS_URL,
                null,
                this::parseWorkoutsResponse,
                error -> {
                    Log.e(TAG, "Error fetching workouts: " + error.getMessage());
                    Toast.makeText(this, "Failed to fetch workouts", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    /**
     * Parses the JSON response from the server and updates the RecyclerView.
     *
     * @param response The JSON response containing workout data.
     */
    /**
     * Parses the JSON response from the server and updates the RecyclerView.
     *
     * @param response The JSON response containing workout data.
     */
    private void parseWorkoutsResponse(JSONArray response) {
        workoutList.clear();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject workoutJson = response.getJSONObject(i);

                // Parse the ID as an integer
                int id = workoutJson.optInt("id", -1); // Default to -1 if the ID is missing or invalid
                String name = workoutJson.optString("name", "Unnamed Workout");
                String description = workoutJson.optString("description", "No Description");
                JSONArray liftsArray = workoutJson.optJSONArray("lifts");

                ArrayList<ObjectLift> lifts = new ArrayList<>();
                if (liftsArray != null) {
                    for (int j = 0; j < liftsArray.length(); j++) {
                        JSONObject liftJson = liftsArray.getJSONObject(j);
                        lifts.add(new ObjectLift(
                                liftJson.optString("title", "Unknown Lift"),
                                liftJson.optString("description", ""),
                                liftJson.optString("type", ""),
                                liftJson.optString("muscleGroup", ""),
                                liftJson.optString("equipment", ""),
                                liftJson.optString("level", "")
                        ));
                    }
                }

                // Pass the ID as an int to the ObjectWorkout constructor
                workoutList.add(new ObjectWorkout(id, name, description, lifts));
                Log.d(TAG, "Added workout: " + name);
            }
            workoutAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing workouts: " + e.getMessage());
        }
    }


    /**
     * Opens the details of a selected workout.
     *
     * @param workout The selected workout.
     */
    private void openWorkoutDetails(ObjectWorkout workout) {
        Intent intent = new Intent(this, ActivityWorkoutDetails.class);
        intent.putExtra("workoutName", workout.getName());
        intent.putExtra("description", workout.getDescription());
        intent.putExtra("liftList", new ArrayList<>(workout.getLifts())); // Pass the list of lifts
        startActivity(intent);
    }

    /**
     * Shows a confirmation dialog before deleting a workout.
     *
     * @param workout The workout to be deleted.
     */
    private void confirmDeleteWorkout(ObjectWorkout workout) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Workout")
                .setMessage("Are you sure you want to delete this workout?")
                .setPositiveButton("Yes", (dialog, which) -> deleteWorkout(workout))
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Deletes the selected workout from the server.
     *
     * @param workout The workout to be deleted.
     */
    private void deleteWorkout(ObjectWorkout workout) {
        String url = DELETE_WORKOUT_URL + Uri.encode(workout.getName());
        JsonObjectRequest deleteRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                response -> {
                    Toast.makeText(this, "Workout deleted", Toast.LENGTH_SHORT).show();
                    fetchWorkouts();
                },
                error -> {
                    Toast.makeText(this, "Error deleting workout", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error deleting workout: " + error.getMessage());
                }
        );
        Volley.newRequestQueue(this).add(deleteRequest);
    }

    /**
     * Navigates to the New Workout activity.
     */
    private void navigateToNewWorkout() {
        Intent intent = new Intent(this, ActivityNewWorkout.class);
        startActivity(intent);
    }
}
