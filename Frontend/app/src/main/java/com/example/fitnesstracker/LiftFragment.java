// LiftFragment.java

package com.example.fitnesstracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying and managing the sets of a specific lift in a workout.
 * This fragment allows users to input reps and weight, add them to the list of sets,
 * and send the data to the backend.
 */
public class LiftFragment extends Fragment {

    private String liftName, completedWorkoutName, username;
    private ObjectActiveWorkout activeWorkout;
    private List<ObjectSet> sets;
    private RecyclerView recyclerView;
    private AdapterSet setAdapter;

    /**
     * Factory method to create a new instance of LiftFragment.
     *
     * @param liftName            The name of the lift.
     * @param completedWorkoutName The name of the completed workout.
     * @param activeWorkout       The active workout object.
     * @return A new instance of LiftFragment.
     */
    public static LiftFragment newInstance(String liftName, String completedWorkoutName, ObjectActiveWorkout activeWorkout) {
        LiftFragment fragment = new LiftFragment();
        Bundle args = new Bundle();
        args.putString("liftName", liftName);
        args.putString("completedWorkoutName", completedWorkoutName);
        args.putSerializable("activeWorkout", activeWorkout);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            liftName = getArguments().getString("liftName");
            completedWorkoutName = getArguments().getString("completedWorkoutName");
            activeWorkout = (ObjectActiveWorkout) getArguments().getSerializable("activeWorkout");
            username = ActivityLogin.getUsername(); // Assuming username is stored globally in ActivityLogin
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_lift, container, false);

        // Initialize UI elements
        TextView liftNameTextView = view.findViewById(R.id.liftNameTextView);
        liftNameTextView.setText(liftName);

        // Initialize RecyclerView for displaying sets
        recyclerView = view.findViewById(R.id.recyclerView);
        sets = new ArrayList<>();
        setAdapter = new AdapterSet(sets);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(setAdapter);

        // Setup Add Set button
        Button addSetButton = view.findViewById(R.id.addSetButton);
        addSetButton.setOnClickListener(v -> showAddSetDialog());

        return view;
    }

    /**
     * Shows a dialog to input reps and weight for a new set.
     */
    private void showAddSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Set to " + liftName);

        // Create a layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_set, null);
        builder.setView(dialogView);

        final EditText etReps = dialogView.findViewById(R.id.etReps);
        final EditText etWeight = dialogView.findViewById(R.id.etWeight);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String repsStr = etReps.getText().toString().trim();
            String weightStr = etWeight.getText().toString().trim();

            if (repsStr.isEmpty() || weightStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter both reps and weight.", Toast.LENGTH_SHORT).show();
                return;
            }

            int reps;
            double weight;
            try {
                reps = Integer.parseInt(repsStr);
                weight = Double.parseDouble(weightStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid input. Please enter numeric values.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (reps <= 0 || weight <= 0) {
                Toast.makeText(getContext(), "Reps and weight must be greater than zero.", Toast.LENGTH_SHORT).show();
                return;
            }

            addSetToLiftSet(reps, weight);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    /**
     * Adds a set to the LiftSet by sending a PUT request to the backend.
     *
     * @param reps   Number of repetitions.
     * @param weight Weight used in kilograms.
     */
    private void addSetToLiftSet(int reps, double weight) {
        if (completedWorkoutName == null || completedWorkoutName.trim().isEmpty()) {
            Toast.makeText(getContext(), "Completed workout name is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construct the PUT request URL
        String url;
        try {
            String encodedName = URLEncoder.encode(completedWorkoutName, "UTF-8");
            String encodedLiftName = URLEncoder.encode(liftName, "UTF-8");
            String encodedUsername = URLEncoder.encode(username, "UTF-8");

            url = "http://coms-3090-014.class.las.iastate.edu:8080/completedWorkout/name/"
                    + encodedName
                    + "?LiftName=" + encodedLiftName
                    + "&username=" + encodedUsername;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error encoding URL parameters.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("LiftFragment", "PUT URL: " + url);

        // Construct the JSON request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("reps", reps);
            requestBody.put("weight", weight);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error creating request body.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("LiftFragment", "PUT Request Body: " + requestBody.toString());

        // Create the PUT request
        JsonObjectRequest putRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                requestBody,
                response -> {
                    // Handle successful addition of set
                    Toast.makeText(getContext(), "Set added successfully!", Toast.LENGTH_SHORT).show();
                    Log.d("LiftFragment", "Set added: " + response.toString());

                    // Add the set to the local list and notify adapter
                    ObjectSet newSet = new ObjectSet(reps, weight);
                    sets.add(newSet);
                    setAdapter.notifyItemInserted(sets.size() - 1);
                },
                error -> {
                    // Handle errors
                    String errorMsg = "Error adding set.";
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
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("LiftFragment", "Error adding set: ", error);
                    if (error.networkResponse != null) {
                        Log.e("Volley Error", "Response Code: " + error.networkResponse.statusCode);
                        Log.e("Volley Error", "Response Data: " + new String(error.networkResponse.data));
                    }
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(getContext()).add(putRequest);
    }
}
