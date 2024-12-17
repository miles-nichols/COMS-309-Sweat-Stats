package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CalendarDetails extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CalendarWorkoutAdapter adapter;
    private List<ObjectActiveWorkout> workoutList;
    private String selectedDate;
    private String username;
    private static final String BASE_URL = "http://coms-3090-014.class.las.iastate.edu:8080/calendar/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_details);

        recyclerView = findViewById(R.id.recyclerViewWorkouts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        workoutList = new ArrayList<>();

        selectedDate = getIntent().getStringExtra("date");
        username = getIntent().getStringExtra("username");

        adapter = new CalendarWorkoutAdapter(this, workoutList);
        recyclerView.setAdapter(adapter);

        fetchWorkoutsForDate(selectedDate);

        // Back button functionality
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarDetails.this, ActivityHomeScreen.class);
            startActivity(intent);
            finish(); // Prevent returning to this screen
        });
    }


    private void fetchWorkoutsForDate(String date) {
        String url = BASE_URL + username + "/" + date;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        // Clear the existing workout list
                        workoutList.clear();

                        JSONArray workoutsArray = response.getJSONArray("Workouts");
                        for (int i = 0; i < workoutsArray.length(); i++) {
                            JSONObject workoutJson = workoutsArray.getJSONObject(i);

                            // Extract the workout name and trim the last 25 characters
                            String name = workoutJson.getString("name");
                            if (name.length() > 25) {
                                name = name.substring(0, name.length() - 25);
                            }

                            // Create ObjectActiveWorkout with the trimmed name
                            ObjectActiveWorkout activeWorkout = new ObjectActiveWorkout(name);

                            // Extract completed sets and lifts
                            JSONArray completedSets = workoutJson.getJSONArray("completedSets");
                            for (int j = 0; j < completedSets.length(); j++) {
                                JSONObject completedSet = completedSets.getJSONObject(j);

                                // Extract lift details
                                JSONObject liftJson = completedSet.getJSONObject("lift");
                                ObjectLift lift = new ObjectLift(
                                        liftJson.getString("title"),
                                        liftJson.optString("description"),
                                        liftJson.optString("type"),
                                        liftJson.optString("muscleGroup"),
                                        liftJson.optString("equipment"),
                                        liftJson.optString("level")
                                );

                                // Extract sets (if any)
                                JSONArray setsArray = completedSet.optJSONArray("sets");
                                List<ObjectSet> setsList = new ArrayList<>();
                                if (setsArray != null) {
                                    for (int k = 0; k < setsArray.length(); k++) {
                                        JSONObject setJson = setsArray.getJSONObject(k);
                                        int reps = setJson.getInt("reps");
                                        double weight = setJson.getDouble("weight");
                                        setsList.add(new ObjectSet(reps, weight));
                                    }
                                }

                                // Add the lift and its sets to the workout
                                activeWorkout.addLiftSet(new ObjectLiftSets(lift.getTitle(), setsList));
                            }

                            // Add the active workout to the list
                            workoutList.add(activeWorkout);
                        }

                        // Notify the adapter to refresh the RecyclerView
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("CalendarDetails", "Error parsing workout details", e);
                        Toast.makeText(CalendarDetails.this, "Error loading workout details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("CalendarDetails", "Error fetching workouts: " + error.getMessage(), error);
                    Toast.makeText(CalendarDetails.this, "Error fetching workouts", Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}
