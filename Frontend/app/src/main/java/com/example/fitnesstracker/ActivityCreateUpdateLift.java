package com.example.fitnesstracker;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

/**
 * Activity that allows the user to create or update a lift in the fitness tracker app.
 * This activity provides functionality for checking if a lift exists, creating a new lift,
 * and updating an existing lift. The lift's details are submitted via POST and PUT requests.
 *
 * @author Miles Nichols
 */
public class ActivityCreateUpdateLift extends AppCompatActivity {

    private EditText titleEditText;
    private EditText typeEditText;
    private EditText levelEditText;
    private EditText descriptionEditText;
    private EditText muscleGroupEditText;
    private EditText equipmentEditText;
    private Button createButton;
    private Button updateButton;
    private RequestQueue requestQueue;
    private boolean isUpdate;
    private String liftTitle;

    /**
     * Called when the activity is created. Initializes the UI elements, checks if a lift
     * is being created or updated, and sets the appropriate button functionality.
     *
     * @param savedInstanceState The saved instance state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_update_lift);

        titleEditText = findViewById(R.id.editTextTitle);
        typeEditText = findViewById(R.id.editTextType);
        levelEditText = findViewById(R.id.editTextLevel);
        descriptionEditText = findViewById(R.id.editTextDescription);
        muscleGroupEditText = findViewById(R.id.editTextMuscleGroup);
        equipmentEditText = findViewById(R.id.editTextEquipment);
        createButton = findViewById(R.id.buttonCreateLift);
        updateButton = findViewById(R.id.buttonUpdateLift);
        requestQueue = Volley.newRequestQueue(this);

        if (getIntent().getExtras() != null) {
            liftTitle = getIntent().getStringExtra("liftTitle");
            checkLiftExists(liftTitle);
        } else {
            isUpdate = false;
            updateButton.setEnabled(false);
        }
        createButton.setOnClickListener(view -> createLift());
        updateButton.setOnClickListener(view -> updateLift());
    }

    /**
     * Checks if a lift with the given title exists in the backend.
     * If the lift exists, the activity switches to update mode and populates the fields.
     *
     * @param title The title of the lift to check for existence.
     */
    private void checkLiftExists(String title) {
        String backendUrl = "http://coms-3090-014.class.las.iastate.edu:8080/lift/title/" + title;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET, backendUrl, null,
                response -> {
                    isUpdate = true;
                    populateFields(title);
                    createButton.setEnabled(false);
                    updateButton.setEnabled(true);
                },
                error -> {
                    Log.e("CreateUpdateLift", "Lift check error: " + error.toString());
                    isUpdate = false;
                    createButton.setEnabled(true);
                    updateButton.setEnabled(false);
                });

        requestQueue.add(jsonRequest);
    }

    /**
     * Populates the form fields with the lift's existing data.
     * Currently only sets the title, but this can be extended to set other fields.
     *
     * @param title The title of the lift to populate in the form.
     */
    private void populateFields(String title) {
        titleEditText.setText(title);
    }

    /**
     * Creates a new lift by sending a POST request with the data entered by the user.
     * The data is serialized into a JSON object and sent to the backend to create the lift.
     */
    private void createLift() {
        String backendUrl = "http://coms-3090-014.class.las.iastate.edu:8080/lift";
        JSONObject liftData = createLiftJson();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST, backendUrl, liftData,
                response -> Toast.makeText(ActivityCreateUpdateLift.this, "Lift created successfully!", Toast.LENGTH_SHORT).show(),
                error -> {
                    error.printStackTrace();
                    Toast.makeText(ActivityCreateUpdateLift.this, "Error creating lift", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonRequest);
    }

    /**
     * Updates an existing lift by sending a PUT request with the updated data.
     * The data is serialized into a JSON object and sent to the backend to update the lift.
     */
    private void updateLift() {
        String backendUrl = "http://coms-3090-014.class.las.iastate.edu:8080/lift/title/" + liftTitle;
        JSONObject liftData = createLiftJson();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.PUT, backendUrl, liftData,
                response -> Toast.makeText(ActivityCreateUpdateLift.this, "Lift updated successfully!", Toast.LENGTH_SHORT).show(),
                error -> {
                    error.printStackTrace();
                    Toast.makeText(ActivityCreateUpdateLift.this, "Error updating lift", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonRequest);
    }

    /**
     * Creates a JSON object containing the lift data entered by the user.
     *
     * @return The JSON object representing the lift data.
     */
    private JSONObject createLiftJson() {
        JSONObject liftData = new JSONObject();
        try {
            liftData.put("title", titleEditText.getText().toString().trim());
            liftData.put("type", typeEditText.getText().toString().trim());
            liftData.put("level", levelEditText.getText().toString().trim());
            liftData.put("description", descriptionEditText.getText().toString().trim());
            liftData.put("muscleGroup", muscleGroupEditText.getText().toString().trim());
            liftData.put("equipment", equipmentEditText.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating JSON object", Toast.LENGTH_SHORT).show();
        }
        return liftData;
    }
}
