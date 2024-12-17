// ActivityStartWorkout.java

package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity for starting a workout by selecting from saved workouts.
 */
public class ActivityStartWorkout extends AppCompatActivity implements AdapterWorkout.WorkoutClickCallback {

    private RecyclerView recyclerView;
    private AdapterWorkout workoutAdapter;
    private List<ObjectWorkout> workoutList;
    private ObjectActiveWorkout activeWorkout;
    private boolean isPastWorkout;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_workout);

        selectedDate = getIntent().getStringExtra("selectedDate");
        activeWorkout = new ObjectActiveWorkout("Active Workout");
        if (selectedDate != null) {
            activeWorkout.setDate(parseDate(selectedDate));
            isPastWorkout = true;
        } else {
            activeWorkout.setDate(new Date());
            isPastWorkout = false;
        }

        recyclerView = findViewById(R.id.workoutRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        workoutList = new ArrayList<>();
        // Initialize AdapterWorkout without RemoveWorkoutCallback
        workoutAdapter = new AdapterWorkout(this, workoutList, this);
        recyclerView.setAdapter(workoutAdapter);

        // Set up Back Button
        findViewById(R.id.btnBack).setOnClickListener(view -> finish());

        fetchWorkouts();
    }

    /**
     * Parses the date string into a Date object.
     *
     * @param dateString The date string in "yyyy-MM-dd" format.
     * @return The parsed Date object.
     */
    private Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * Fetches the list of saved workouts from the backend.
     */
    private void fetchWorkouts() {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/workouts";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    workoutList.clear();
                    workoutList.addAll(parseWorkouts(response));
                    workoutAdapter.notifyDataSetChanged();
                    Log.d("ActivityStartWorkout", "Fetched " + workoutList.size() + " workouts.");
                },
                error -> {
                    Toast.makeText(ActivityStartWorkout.this, "Error fetching workouts", Toast.LENGTH_SHORT).show();
                    Log.e("StartWorkoutActivity", "Error fetching workouts: " + error.getMessage());
                });

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    /**
     * Parses the JSON response into a list of ObjectWorkout.
     *
     * @param response The JSON array response from the backend.
     * @return A list of ObjectWorkout instances.
     */
    private List<ObjectWorkout> parseWorkouts(JSONArray response) {
        List<ObjectWorkout> workouts = new ArrayList<>();

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject workoutJson = response.getJSONObject(i);

                // Parse id as an integer
                int id = workoutJson.optInt("id", -1); // Default to -1 if id is not valid
                String name = workoutJson.optString("name", "Unknown Workout");
                String description = workoutJson.optString("description", "No Description");

                JSONArray liftsJsonArray = workoutJson.optJSONArray("lifts");
                List<ObjectLift> lifts = new ArrayList<>();

                if (liftsJsonArray != null) {
                    for (int j = 0; j < liftsJsonArray.length(); j++) {
                        JSONObject liftJson = liftsJsonArray.getJSONObject(j);
                        String title = liftJson.optString("title", "Unknown Lift");
                        String liftDescription = liftJson.optString("description", "No Description");
                        String type = liftJson.optString("type", "Unknown Type");
                        String muscleGroup = liftJson.optString("muscleGroup", "Unknown Muscle Group");
                        String equipment = liftJson.optString("equipment", "Unknown Equipment");
                        String level = liftJson.optString("level", "Unknown Level");

                        ObjectLift lift = new ObjectLift(title, liftDescription, type, muscleGroup, equipment, level);
                        lifts.add(lift);
                    }
                }

                ObjectWorkout workout = new ObjectWorkout(id, name, description, lifts);
                workouts.add(workout);
                Log.d("ActivityStartWorkout", "Added workout: " + name);
            } catch (JSONException e) {
                Log.e("StartWorkoutActivity", "JSON Parsing error: " + e.getMessage());
            }
        }
        return workouts;
    }


    /**
     * Handles the workout item click to start the selected workout.
     *
     * @param workout The selected ObjectWorkout.
     */
    @Override
    public void onWorkoutClicked(ObjectWorkout workout) {
        String username = ActivityLogin.getUsername();
        activeWorkout.setName(workout.getName());
        Intent intent = new Intent(this, ActivityLift.class);
        intent.putExtra("workout", workout);
        intent.putExtra("activeWorkout", activeWorkout);
        intent.putExtra("selectedDate", selectedDate);
        intent.putExtra("isPastWorkout", isPastWorkout);
        startActivity(intent);
    }

    boolean getIsPastWorkout(){
        return isPastWorkout;
    }
}
