package com.example.fitnesstracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ActivityLiftSearch extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterLiftSearch adapter;
    private List<HashMap<String, String>> liftList = new ArrayList<>();
    private List<HashMap<String, String>> filteredList = new ArrayList<>();
    private EditText searchBar;
    private Switch filterByEquipmentSwitch, filterByMuscleGroupSwitch, filterByTypeSwitch;
    private String selectedEquipment, selectedMuscleGroup, selectedType;
    private static final int VIEW_DETAIL_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lift_search);

        recyclerView = findViewById(R.id.recyclerViewLifts);
        searchBar = findViewById(R.id.search_bar);
        filterByEquipmentSwitch = findViewById(R.id.switch_filter_by_equipment);
        filterByMuscleGroupSwitch = findViewById(R.id.switch_filter_by_muscle_group);
        filterByTypeSwitch = findViewById(R.id.switch_filter_by_type);

        Button btnBackToWorkout = findViewById(R.id.btnBackToWorkout);
        btnBackToWorkout.setOnClickListener(v -> onBackPressed());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterLiftSearch(
                filteredList,
                lift -> { // Add lift to workout and return result
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selectedLift", lift);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                },
                lift -> { // View details
                    viewLiftDetails(lift);
                },
                ActivityLiftSearch.this
        );
        recyclerView.setAdapter(adapter);

        // Fetch all lifts from the backend
        fetchAllLifts();

        // Handle filter changes
        handleFilters();

        // Set up real-time search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                applyFilters(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIEW_DETAIL_REQUEST_CODE && resultCode == RESULT_OK) {
            // The user chose to add the lift to the workout from the detail screen
            HashMap<String, String> selectedLift = (HashMap<String, String>) data.getSerializableExtra("selectedLift");
            if (selectedLift != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedLift", selectedLift);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
    }

    private void fetchAllLifts() {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/lift";
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    liftList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject liftJson = response.getJSONObject(i);
                            HashMap<String, String> lift = new HashMap<>();
                            lift.put("title", liftJson.getString("title"));
                            lift.put("muscleGroup", liftJson.optString("muscleGroup", ""));
                            lift.put("equipment", liftJson.optString("equipment", ""));
                            lift.put("type", liftJson.optString("type", ""));
                            liftList.add(lift);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    sortLiftsAlphabetically();
                    applyFilters(searchBar.getText().toString());
                },
                error -> Toast.makeText(ActivityLiftSearch.this, "Error fetching lifts", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void sortLiftsAlphabetically() {
        Collections.sort(liftList, (o1, o2) -> o1.get("title").compareToIgnoreCase(o2.get("title")));
    }

    private void applyFilters(String query) {
        filteredList.clear();
        for (HashMap<String, String> lift : liftList) {
            boolean matches = lift.get("title").toLowerCase().contains(query.toLowerCase()) &&
                    (selectedMuscleGroup == null || lift.get("muscleGroup").equalsIgnoreCase(selectedMuscleGroup)) &&
                    (selectedEquipment == null || lift.get("equipment").equalsIgnoreCase(selectedEquipment)) &&
                    (selectedType == null || lift.get("type").equalsIgnoreCase(selectedType));
            if (matches) {
                filteredList.add(lift);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void handleFilters() {
        filterByEquipmentSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showFilterDialog("Equipment", new String[]{"Barbell", "Dumbbell", "Cable"}, value -> {
                    selectedEquipment = value;
                    applyFilters(searchBar.getText().toString());
                });
            } else {
                selectedEquipment = null;
                applyFilters(searchBar.getText().toString());
            }
        });

        filterByMuscleGroupSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showFilterDialog("Muscle Group", new String[]{"Legs", "Chest", "Arms"}, value -> {
                    selectedMuscleGroup = value;
                    applyFilters(searchBar.getText().toString());
                });
            } else {
                selectedMuscleGroup = null;
                applyFilters(searchBar.getText().toString());
            }
        });

        filterByTypeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showFilterDialog("Type", new String[]{"Strength", "Cardio", "Flexibility"}, value -> {
                    selectedType = value;
                    applyFilters(searchBar.getText().toString());
                });
            } else {
                selectedType = null;
                applyFilters(searchBar.getText().toString());
            }
        });
    }

    private void showFilterDialog(String filterType, String[] options, FilterCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select " + filterType);
        builder.setItems(options, (dialog, which) -> {
            callback.onFilterSelected(options[which]);
            applyFilters(searchBar.getText().toString());
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            applyFilters(searchBar.getText().toString());
        });
        builder.create().show();
    }

    private void viewLiftDetails(HashMap<String, String> lift) {
        String liftTitle = lift.get("title");
        Intent intent = new Intent(ActivityLiftSearch.this, ActivityLiftDetail.class);
        intent.putExtra("liftTitle", liftTitle);
        startActivityForResult(intent, VIEW_DETAIL_REQUEST_CODE);
    }

    interface FilterCallback {
        void onFilterSelected(String value);
    }
}
