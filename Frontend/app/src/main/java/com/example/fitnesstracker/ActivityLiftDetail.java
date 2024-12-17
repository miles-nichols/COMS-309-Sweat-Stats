package com.example.fitnesstracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.toolbox.JsonArrayRequest;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityLiftDetail extends AppCompatActivity {

    private TextView tvLiftTitle, tvLiftDescription, tvLiftType, tvLiftMuscleGroup, tvLiftEquipment, tvLiftLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lift_detail);

        // Initialize TextViews
        tvLiftTitle = findViewById(R.id.tvLiftTitle);
        tvLiftDescription = findViewById(R.id.tvLiftDescription);
        tvLiftType = findViewById(R.id.tvLiftType);
        tvLiftMuscleGroup = findViewById(R.id.tvLiftMuscleGroup);
        tvLiftEquipment = findViewById(R.id.tvLiftEquipment);
        tvLiftLevel = findViewById(R.id.tvLiftLevel);

        // Back Button Setup
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> finish());

        // Get the lift title from the intent
        String liftTitle = getIntent().getStringExtra("liftTitle");

        // Fetch lift details from server
        fetchLiftDetails(liftTitle);
    }

    private void fetchLiftDetails(String liftTitle) {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/lift/title/" + liftTitle;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.length() > 0) {
                            JSONObject liftJson = response.getJSONObject(0);
                            tvLiftTitle.setText(liftJson.getString("title"));
                            tvLiftDescription.setText(liftJson.getString("description"));
                            tvLiftType.setText(liftJson.getString("type"));
                            tvLiftMuscleGroup.setText(liftJson.getString("muscleGroup"));
                            tvLiftEquipment.setText(liftJson.getString("equipment"));
                            tvLiftLevel.setText(liftJson.getString("level"));
                        } else {
                            Toast.makeText(ActivityLiftDetail.this, "No details found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ActivityLiftDetail.this, "Error loading details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(ActivityLiftDetail.this, "Error fetching lift details", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

}
