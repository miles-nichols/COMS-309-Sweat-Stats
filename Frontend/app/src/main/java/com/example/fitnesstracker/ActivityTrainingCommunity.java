// ActivityTrainingCommunity.java
package com.example.fitnesstracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ActivityTrainingCommunity extends AppCompatActivity implements AdapterTrainingCommunity.OnCommunityActionListener {

    private static final String TAG = "ActivityTrainingCommunity";

    private RecyclerView recyclerView;
    private AdapterTrainingCommunity adapter;
    private List<HashMap<String, String>> communityList = new ArrayList<>();
    private List<HashMap<String, String>> filteredList = new ArrayList<>();
    private EditText searchBar;
    private ImageButton btnBack, btnAddCommunity;

    private String currentUserUsername = null;
    private String currentUserRole = "USER"; // Default role

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_community);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewCommunities);
        searchBar = findViewById(R.id.searchBar);
        btnBack = findViewById(R.id.btnBack);
        btnAddCommunity = findViewById(R.id.btnAddCommunity); // Add Community Button

        // Initially hide the Add Community button
        btnAddCommunity.setVisibility(View.GONE);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Adapter will be initialized after fetching user role
        adapter = new AdapterTrainingCommunity(filteredList, this, currentUserRole, currentUserUsername, this);
        recyclerView.setAdapter(adapter);

        // Back button functionality
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, SocialHubActivity.class);
            startActivity(intent);
            finish();
        });

        // Add Community button functionality (Visible only to Trainers)
        btnAddCommunity.setOnClickListener(v -> showAddCommunityDialog());

        // Fetch current user's username from Login Activity
        currentUserUsername = ActivityLogin.getUsername(); // Ensure this method retrieves the correct username

        if (currentUserUsername == null || currentUserUsername.isEmpty()) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            // Redirect to login or handle appropriately
            Intent intent = new Intent(this, ActivityLogin.class);
            startActivity(intent);
            finish();
            return;
        }

        // Fetch user's role
        fetchUserRole();

        // Search functionality
        searchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // Not needed
            }
        });
    }

    private void fetchUserRole() {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/user/" + Uri.encode(currentUserUsername);

        Log.d(TAG, "Fetching user role with URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d(TAG, "User Role Response: " + response.toString());
                    try {
                        currentUserRole = response.getString("role");
                        if (currentUserRole.equalsIgnoreCase("TRAINER")) {
                            btnAddCommunity.setVisibility(View.VISIBLE);
                            fetchCommunitiesByTrainer();
                        } else {
                            btnAddCommunity.setVisibility(View.GONE);
                            fetchAllCommunities();
                        }
                        // Initialize adapter with updated role and username
                        adapter = new AdapterTrainingCommunity(filteredList, this, currentUserRole, currentUserUsername, this);
                        recyclerView.setAdapter(adapter);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing user role JSON", e);
                        Toast.makeText(this, "Error fetching user role", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching user role", error);
                    Toast.makeText(this, "Error fetching user role", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void fetchAllCommunities() {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/trainingCommunity";

        Log.d(TAG, "Fetching all communities with URL: " + url);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    communityList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonCommunity = response.getJSONObject(i);
                            HashMap<String, String> community = new HashMap<>();
                            community.put("title", jsonCommunity.getString("title"));
                            community.put("trainer", jsonCommunity.getJSONObject("trainer").getString("username"));

                            // Decode the description to replace %20 with spaces
                            String encodedDescription = jsonCommunity.getString("description");
                            String decodedDescription = URLDecoder.decode(encodedDescription, StandardCharsets.UTF_8.name());
                            community.put("description", decodedDescription);

                            // Check if the user is a member
                            JSONArray members = jsonCommunity.getJSONArray("members"); // Assuming 'members' array exists
                            boolean isMember = false;
                            for (int j = 0; j < members.length(); j++) {
                                JSONObject member = members.getJSONObject(j);
                                if (member.getString("username").equals(currentUserUsername)) {
                                    isMember = true;
                                    break;
                                }
                            }
                            community.put("isMember", String.valueOf(isMember));
                            communityList.add(community);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing community JSON", e);
                        }
                    }
                    sortCommunitiesAlphabetically();
                    applyFilters(searchBar.getText().toString());
                },
                error -> {
                    Toast.makeText(this, "Error fetching communities", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching communities", error);
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void fetchCommunitiesByTrainer() {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/trainingCommunity/trainer/" + Uri.encode(currentUserUsername);

        Log.d(TAG, "Fetching trainer's communities with URL: " + url);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    communityList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonCommunity = response.getJSONObject(i);
                            HashMap<String, String> community = new HashMap<>();
                            community.put("title", jsonCommunity.getString("title"));
                            community.put("trainer", jsonCommunity.getJSONObject("trainer").getString("username"));

                            // Decode the description to replace %20 with spaces
                            String encodedDescription = jsonCommunity.getString("description");
                            String decodedDescription = URLDecoder.decode(encodedDescription, StandardCharsets.UTF_8.name());
                            community.put("description", decodedDescription);

                            // Trainers only see their own communities; no need to track 'isMember'
                            communityList.add(community);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing community JSON", e);
                        }
                    }
                    sortCommunitiesAlphabetically();
                    applyFilters(searchBar.getText().toString());
                },
                error -> {
                    Toast.makeText(this, "Error fetching trainer's communities", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching trainer's communities", error);
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void sortCommunitiesAlphabetically() {
        Collections.sort(communityList, (o1, o2) -> {
            // Safely compare "isMember" values, treating null as false
            boolean isMember1 = "true".equals(o1.get("isMember"));
            boolean isMember2 = "true".equals(o2.get("isMember"));

            if (isMember1 && !isMember2) {
                return -1; // o1 comes before o2
            } else if (!isMember1 && isMember2) {
                return 1; // o2 comes before o1
            } else {
                // Handle null or missing "title" gracefully
                String title1 = o1.get("title") != null ? o1.get("title") : "";
                String title2 = o2.get("title") != null ? o2.get("title") : "";
                return title1.compareToIgnoreCase(title2);
            }
        });
    }

    private void applyFilters(String query) {
        filteredList.clear();
        for (HashMap<String, String> community : communityList) {
            boolean matches = community.get("title").toLowerCase().contains(query.toLowerCase())
                    || community.get("trainer").toLowerCase().contains(query.toLowerCase());
            if (matches) {
                filteredList.add(community);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onJoinCommunity(int position) {
        HashMap<String, String> selectedCommunity = filteredList.get(position);
        String communityTitle = selectedCommunity.get("title");
        String trainerUsername = selectedCommunity.get("trainer");
        joinCommunity(communityTitle, trainerUsername, position);
    }

    @Override
    public void onLeaveCommunity(int position) {
        HashMap<String, String> selectedCommunity = filteredList.get(position);
        String communityTitle = selectedCommunity.get("title");
        String trainerUsername = selectedCommunity.get("trainer");
        leaveCommunity(communityTitle, trainerUsername, position);
    }

    private void joinCommunity(String communityTitle, String trainerUsername, int position) {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/trainingCommunity/" + Uri.encode(currentUserUsername)
                + "?trainerUsername=" + Uri.encode(trainerUsername)
                + "&communityTitle=" + Uri.encode(communityTitle);

        Log.d(TAG, "Joining community with URL: " + url);

        // Using PUT as per the endpoint specification
        StringRequest request = new StringRequest(
                Request.Method.PUT,
                url,
                response -> {
                    Log.d(TAG, "Join Community Response: " + response);
                    Toast.makeText(this, "Joined the community successfully!", Toast.LENGTH_SHORT).show();
                    // Update the community's isMember status
                    communityList.get(position).put("isMember", "true");
                    // Re-sort the list
                    sortCommunitiesAlphabetically();
                    // Notify adapter of data change
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    Log.e(TAG, "Error joining community", error);
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        String errorMessage = "Error joining community.";
                        if (statusCode == 400) {
                            errorMessage = "Bad Request. Please check the input parameters.";
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void leaveCommunity(String communityTitle, String trainerUsername, int position) {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/trainingCommunity/" + Uri.encode(currentUserUsername)
                + "?trainerUsername=" + Uri.encode(trainerUsername)
                + "&communityTitle=" + Uri.encode(communityTitle);

        Log.d(TAG, "Leaving community with URL: " + url);

        // Using DELETE as per the endpoint specification
        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                url,
                response -> {
                    Log.d(TAG, "Leave Community Response: " + response);
                    Toast.makeText(this, "Left the community successfully!", Toast.LENGTH_SHORT).show();
                    // Update the community's isMember status
                    communityList.get(position).put("isMember", "false");
                    // Re-sort the list
                    sortCommunitiesAlphabetically();
                    // Notify adapter of data change
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    Log.e(TAG, "Error leaving community", error);
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        String errorMessage = "Error leaving community.";
                        if (statusCode == 400) {
                            errorMessage = "Bad Request. Please check the input parameters.";
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void showAddCommunityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Community");

        // Inflate custom layout for dialog
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_community, null);
        builder.setView(dialogView);

        EditText etCommunityName = dialogView.findViewById(R.id.etCommunityName);
        EditText etCommunityDescription = dialogView.findViewById(R.id.etCommunityDescription);

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        // Override the positive button to prevent automatic dismissal
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                String communityName = etCommunityName.getText().toString().trim();
                String communityDescription = etCommunityDescription.getText().toString().trim();

                if (communityName.isEmpty() || communityDescription.isEmpty()) {
                    Toast.makeText(ActivityTrainingCommunity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    createNewCommunity(communityName, communityDescription);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private void createNewCommunity(String communityName, String communityDescription) {
        // Construct the URL with query parameters
        Uri.Builder builder = Uri.parse("http://coms-3090-014.class.las.iastate.edu:8080/trainingCommunity")
                .buildUpon()
                .appendQueryParameter("trainerUsername", Uri.encode(currentUserUsername))
                .appendQueryParameter("communityTitle", Uri.encode(communityName))
                .appendQueryParameter("description", Uri.encode(communityDescription));
        String url = builder.build().toString();

        Log.d(TAG, "Creating new community with URL: " + url);

        // Using POST with JSON payload as per REST conventions
        JSONObject payload = new JSONObject();
        try {
            payload.put("trainerUsername", currentUserUsername);
            payload.put("communityTitle", communityName);
            payload.put("description", communityDescription);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                payload,
                response -> {
                    Log.d(TAG, "Create Community Response: " + response.toString());
                    Toast.makeText(this, "Community created successfully!", Toast.LENGTH_SHORT).show();
                    // Refresh the community list
                    if (currentUserRole.equalsIgnoreCase("TRAINER")) {
                        fetchCommunitiesByTrainer();
                    } else {
                        fetchAllCommunities();
                    }
                },
                error -> {
                    Log.e(TAG, "Error creating community", error);
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        String errorMessage = "Error creating community.";
                        if (statusCode == 400) {
                            errorMessage = "Bad Request. Please check the input parameters.";
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
