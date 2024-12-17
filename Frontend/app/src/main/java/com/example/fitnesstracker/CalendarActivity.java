package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class CalendarActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private CalendarView calendarView;
    Set<String> completedWorkoutDays;
    private String username = ActivityLogin.getUsername();
    private static final String BASE_URL = "http://coms-3090-014.class.las.iastate.edu:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);

        btnBack = findViewById(R.id.btnBack);

        calendarView = findViewById(R.id.calendarView);
        completedWorkoutDays = new HashSet<>();
        calendarView.setEnabled(false);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityHomeScreen.class);
            startActivity(intent);
            finish();
        });

        fetchCompletedWorkoutsForUser();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            if (completedWorkoutDays.contains(selectedDate)) {
                showCompletedWorkoutDetails(selectedDate);
            } else {
                Intent intent = new Intent(CalendarActivity.this, ActivityStartWorkout.class);
                intent.putExtra("selectedDate", selectedDate);
                startActivity(intent);
            }
        });
    }

    private void fetchCompletedWorkoutsForUser() {
        String url = BASE_URL + "/completedWorkout/user/" + username;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        completedWorkoutDays.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject workout = response.getJSONObject(i);
                            String date = workout.getString("date");
                            completedWorkoutDays.add(date);
                        }

                        calendarView.setEnabled(true);
                        Log.d("CalendarActivity", "Completed workouts: " + completedWorkoutDays);
                    } catch (Exception e) {
                        Log.e("CalendarActivity", "Error parsing workout dates", e);
                    }
                },
                error -> {
                    Log.e("CalendarActivity", "Error fetching workout dates: " + error.getMessage(), error);
                    if (error.networkResponse != null) {
                        Log.e("CalendarActivity", "Response Code: " + error.networkResponse.statusCode);
                        Log.e("CalendarActivity", "Response Data: " + new String(error.networkResponse.data));
                    }
                }
        );

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void showCompletedWorkoutDetails(String date) {
        Intent intent = new Intent(this, CalendarDetails.class);
        intent.putExtra("date", date);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
