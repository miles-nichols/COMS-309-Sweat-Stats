package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.net.Uri;
import com.android.volley.toolbox.StringRequest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityWorkoutDetails extends AppCompatActivity {
    private static final int LIFT_SEARCH_REQUEST_CODE = 100;

    private RecyclerView recyclerView;
    private AdapterLift adapterLift;
    private List<ObjectLift> liftList;
    private String workoutName;          // Name currently used by server
    private String originalWorkoutName;  // Original name from intent (for reference)
    private EditText etWorkoutName;      // Editable workout name field
    private EditText etWorkoutDescription; // Editable workout description field
    private Button btnSaveWorkout;
    private FloatingActionButton btnAddLift;

    // To track current description from server
    private String currentDescription = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details);

        // Retrieve workout name and lift list from Intent
        workoutName = getIntent().getStringExtra("workoutName");
        originalWorkoutName = workoutName; // Keep a record of the original name
        liftList = (List<ObjectLift>) getIntent().getSerializableExtra("liftList");

        if (liftList == null) {
            liftList = new ArrayList<>();
        }

        // Set up RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerViewLifts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterLift = new AdapterLift(liftList, this::removeLift);
        recyclerView.setAdapter(adapterLift);

        // Initialize EditTexts for name and description
        etWorkoutName = findViewById(R.id.etWorkoutName);
        etWorkoutDescription = findViewById(R.id.etWorkoutDescription);

        // Set initial name in EditText
        etWorkoutName.setText(workoutName);

        // Set up Save Workout button
        btnSaveWorkout = findViewById(R.id.btnSaveWorkout);
        btnSaveWorkout.setOnClickListener(v -> saveWorkout());

        // Set up Add Lift FAB
        btnAddLift = findViewById(R.id.btnAddLift);
        btnAddLift.setOnClickListener(v -> navigateToLiftSearch());

        // Fetch current workout details from backend
        fetchWorkoutDetails();
    }

    private void fetchWorkoutDetails() {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/workout/" + originalWorkoutName;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // Parse response for current name and description
                    String fetchedName = response.optString("name", workoutName);
                    currentDescription = response.optString("description", "");

                    // Update UI fields
                    etWorkoutName.setText(fetchedName);
                    etWorkoutDescription.setText(currentDescription);
                },
                error -> Toast.makeText(this, "Error fetching workout details", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(getRequest);
    }

    private void removeLift(ObjectLift lift) {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/workout/" + workoutName + "/lift/" + lift.getTitle();
        JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    liftList.remove(lift);
                    adapterLift.notifyDataSetChanged();
                    Toast.makeText(this, "Lift removed: " + lift.getTitle(), Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(this, "Error removing lift: " + error.getMessage(), Toast.LENGTH_SHORT).show());
        Volley.newRequestQueue(this).add(deleteRequest);
    }

    private void saveWorkout() {
        String newName = etWorkoutName.getText().toString().trim();
        String newDescription = etWorkoutDescription.getText().toString().trim();

        // Update name if changed
        if (!newName.equals(originalWorkoutName)) {
            updateWorkoutName(originalWorkoutName, newName, nameSuccess -> {
                if (nameSuccess) {
                    // If name updated, set references
                    workoutName = newName;
                    originalWorkoutName = newName;
                    // Now update description
                    updateWorkoutDescription(newDescription, descSuccess -> {
                        // Then add lifts
                        addLiftsToWorkout();
                    });
                } else {
                    Toast.makeText(this, "Failed to update workout name", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Name not changed, just update description
            updateWorkoutDescription(newDescription, descSuccess -> {
                // Then add lifts
                addLiftsToWorkout();
            });
        }
    }

    private void updateWorkoutName(String oldName, String newName, UpdateCallback callback) {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/workout/" + oldName + "?newName=" + Uri.encode(newName);
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, url, null,
                response -> {
                    Toast.makeText(this, "Workout name updated!", Toast.LENGTH_SHORT).show();
                    callback.onUpdateCompleted(true);
                },
                error -> {
                    Toast.makeText(this, "Error updating workout name", Toast.LENGTH_SHORT).show();
                    callback.onUpdateCompleted(false);
                }
        );
        Volley.newRequestQueue(this).add(putRequest);
    }

    private void updateWorkoutDescription(String newDescription, UpdateCallback callback) {
        // Consider URL-encoding the description
        String encodedDescription = Uri.encode(newDescription);
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/workout/" + workoutName
                + "/description?description=" + encodedDescription;

        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                response -> {
                    Toast.makeText(this, "Workout description updated!", Toast.LENGTH_SHORT).show();
                    currentDescription = newDescription;
                    callback.onUpdateCompleted(true);
                },
                error -> {
                    Toast.makeText(this, "Error updating description: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onUpdateCompleted(false);
                }
        );

        Volley.newRequestQueue(this).add(putRequest);
    }

    private void addLiftsToWorkout() {
        if (liftList.isEmpty()) {
            // No lifts to add, just end here
            Toast.makeText(this, "Workout saved successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ActivityMyWorkouts.class);
            startActivity(intent);
            finish();
            return;
        }

        final int[] completedRequests = {0};
        for (ObjectLift lift : liftList) {
            String url = "http://coms-3090-014.class.las.iastate.edu:8080/workout/" + workoutName + "?liftName=" + Uri.encode(lift.getTitle());
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                    response -> {
                        completedRequests[0]++;
                        if (completedRequests[0] == liftList.size()) {
                            Toast.makeText(this, "Workout saved successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, ActivityMyWorkouts.class);
                            startActivity(intent);
                            finish();
                        }
                    },
                    error -> {
                        Toast.makeText(this, "Error adding lift: " + lift.getTitle(), Toast.LENGTH_SHORT).show();
                        completedRequests[0]++;
                        if (completedRequests[0] == liftList.size()) {
                            Toast.makeText(this, "Workout saved with some errors!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, ActivityMyWorkouts.class);
                            startActivity(intent);
                            finish();
                        }
                    }
            );
            Volley.newRequestQueue(this).add(postRequest);
        }
    }

    private void navigateToLiftSearch() {
        Intent intent = new Intent(this, ActivityLiftSearch.class);
        startActivityForResult(intent, LIFT_SEARCH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LIFT_SEARCH_REQUEST_CODE && resultCode == RESULT_OK) {
            HashMap<String, String> selectedLift = (HashMap<String, String>) data.getSerializableExtra("selectedLift");
            if (selectedLift != null) {
                liftList.add(new ObjectLift(
                        selectedLift.get("title"),
                        selectedLift.get("description"),
                        selectedLift.get("type"),
                        selectedLift.get("muscleGroup"),
                        selectedLift.get("equipment"),
                        "" // No level provided
                ));
                adapterLift.notifyDataSetChanged();
            }
        }
    }

    interface UpdateCallback {
        void onUpdateCompleted(boolean success);
    }
}
