package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SettingsActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText typeInput;
    private EditText incidentInput;
    private Button reportButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        usernameInput = findViewById(R.id.usernameInput);
        typeInput = findViewById(R.id.typeInput);
        incidentInput = findViewById(R.id.incidentInput);
        reportButton = findViewById(R.id.reportButton);
        logoutButton = findViewById(R.id.logoutButton);

        reportButton.setOnClickListener(view -> {
            String username = usernameInput.getText().toString();
            String type = typeInput.getText().toString();
            String incident = incidentInput.getText().toString();

            if (username.isEmpty() || incident.isEmpty() || type.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                submitReport(username, type, incident);
            }
        });

        logoutButton.setOnClickListener(view -> {
            usernameInput.setText("");
            typeInput.setText("");
            incidentInput.setText("");

            Intent intent = new Intent(SettingsActivity.this, ActivityLogin.class);
            startActivity(intent);
            finish();
        });
    }

    private void submitReport(String reporter, String type, String description) {
        try {
            // URL encode the type and description to ensure special characters are handled
            String encodedType = URLEncoder.encode(type, "UTF-8");
            String encodedDescription = URLEncoder.encode(description, "UTF-8");

            // Construct the URL with reporter and reported as path variables
            String url = "http://coms-3090-014.class.las.iastate.edu:8080/report/" + ActivityLogin.getUsername() + "/" + reporter + "?summary=" + encodedType + "&description=" + encodedDescription;
            Log.d("ReportRequest", "URL: " + url);

            // Creating the StringRequest to send a report
            StringRequest reportRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        // Handle success response
                        Toast.makeText(this, "Report successfully submitted", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        // Log error response
                        Log.e("ReportRequest", "Error: " + error.getMessage());
                        Toast.makeText(this, "Error submitting report", Toast.LENGTH_SHORT).show();
                    }) {
            };

            // Add the request to the Volley queue
            Volley.newRequestQueue(this).add(reportRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error encoding request", Toast.LENGTH_SHORT).show();
        }
    }
}
