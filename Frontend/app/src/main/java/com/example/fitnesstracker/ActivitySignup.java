package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity class for user signup in the Fitness Tracker app.
 * Handles user registration and allows updates to email, username, and password.
 * Communicates with a backend server using Volley for network requests.
 */
public class ActivitySignup extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private static final String BASE_URL = "http://coms-3090-014.class.las.iastate.edu:8080/user/";

    private EditText etUsername, etPassword, etConfirmPassword, etEmail;
    private Button btnSignup, btnChangeEmail, btnChangePassword, btnChangeUsername;

    /**
     * Called when the activity is created. Initializes UI components and sets up button listeners.
     *
     * @param savedInstanceState Contains the saved state of the activity, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        etUsername = findViewById(R.id.etNewUsername);
        etPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etEmail = findViewById(R.id.etEmail);
        btnSignup = findViewById(R.id.btnSignup);
        btnChangeEmail = findViewById(R.id.btnChangeEmail);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangeUsername = findViewById(R.id.btnChangeUserName);

        // Set up button click listeners
        btnSignup.setOnClickListener(v -> handleSignup());
        btnChangeEmail.setOnClickListener(v -> changeEmail());
        btnChangePassword.setOnClickListener(v -> changePassword());
        btnChangeUsername.setOnClickListener(v -> changeUsername());
    }

    /**
     * Handles user signup by validating input and sending a registration request to the server.
     */
    private void handleSignup() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (isInputValid(username, password, confirmPassword, email)) {
            makeJsonRequest(BASE_URL + "signup", username, password, email);
            Intent intent = new Intent(ActivitySignup.this, ActivityLogin.class);
            startActivity(intent);
        }
    }

    /**
     * Validates the user input for signup.
     *
     * @param username        The entered username.
     * @param password        The entered password.
     * @param confirmPassword The confirmation of the entered password.
     * @param email           The entered email address.
     * @return True if input is valid, otherwise false.
     */
    private boolean isInputValid(String username, String password, String confirmPassword, String email) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            showToast("Please fill all fields");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match");
            return false;
        }
        if (!isValidEmail(email)) {
            showToast("Invalid email format");
            return false;
        }
        if (!isValidPassword(password)) {
            showToast("Password must be at least 8 characters long, contain at least one uppercase letter and one special character");
            return false;
        }
        return true;
    }

    /**
     * Sends a JSON request to the server for the signup process.
     *
     * @param url      The endpoint URL for the signup request.
     * @param username The entered username.
     * @param password The entered password.
     * @param email    The entered email address.
     */
    private void makeJsonRequest(String url, String username, String password, String email) {
        JSONObject requestBody = createRequestBody(username, password, email);
        sendRequest(Request.Method.POST, url, requestBody);
    }

    /**
     * Handles email change requests by validating input and sending a request to the server.
     */
    private void changeEmail() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();

        if (isChangeInputValid(username, password, newEmail)) {
            JSONObject requestBody = createEmailChangeRequestBody(username, password, newEmail);
            sendRequest(Request.Method.PUT, BASE_URL + "signup/email", requestBody);
        }
    }

    /**
     * Handles password change requests by validating input and sending a request to the server.
     */
    private void changePassword() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String newPassword = etPassword.getText().toString().trim();

        if (isChangeInputValid(username, email, newPassword)) {
            JSONObject requestBody = createPasswordChangeRequestBody(username, email, newPassword);
            sendRequest(Request.Method.PUT, BASE_URL + "signup/password", requestBody);
        }
    }

    /**
     * Handles username change requests by validating input and sending a request to the server.
     */
    private void changeUsername() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String newUsername = etUsername.getText().toString().trim();

        if (isChangeInputValid(email, password, newUsername)) {
            JSONObject requestBody = createUsernameChangeRequestBody(email, password, newUsername);
            sendRequest(Request.Method.PUT, BASE_URL + "signup/username", requestBody);
        }
    }

    /**
     * Validates the input for change requests.
     *
     * @param firstField  The first field to validate.
     * @param secondField The second field to validate.
     * @param thirdField  The third field to validate.
     * @return True if input is valid, otherwise false.
     */
    private boolean isChangeInputValid(String firstField, String secondField, String thirdField) {
        if (firstField.isEmpty() || secondField.isEmpty() || thirdField.isEmpty()) {
            showToast("Please fill all fields");
            return false;
        }
        return true;
    }

    /**
     * Creates a JSON request body for signup requests.
     *
     * @param username The entered username.
     * @param password The entered password.
     * @param email    The entered email address.
     * @return A JSONObject containing the request body.
     */
    private JSONObject createRequestBody(String username, String password, String email) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
            requestBody.put("password", password);
            requestBody.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestBody;
    }

    private JSONObject createEmailChangeRequestBody(String username, String password, String newEmail) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
            requestBody.put("password", password);
            requestBody.put("email", newEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestBody;
    }

    private JSONObject createPasswordChangeRequestBody(String username, String email, String newPassword) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
            requestBody.put("email", email);
            requestBody.put("password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestBody;
    }

    private JSONObject createUsernameChangeRequestBody(String email, String password, String newUsername) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
            requestBody.put("username", newUsername);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestBody;
    }

    // Similar JSON creation methods for other request types are defined below (omitted for brevity).

    /**
     * Sends a JSON request to the specified URL.
     *
     * @param method       The HTTP method (e.g., POST, PUT).
     * @param url          The endpoint URL.
     * @param requestBody  The JSON request body.
     */
    private void sendRequest(int method, String url, JSONObject requestBody) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                method,
                url,
                requestBody,
                this::handleResponse,
                error -> {
                    Log.e(TAG, "Error: " + error.toString());
                    showToast("Error occurred, please try again later");
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Handles the server response to a JSON request.
     *
     * @param response The JSONObject response from the server.
     */
    private void handleResponse(JSONObject response) {
        try {
            String responseMessage = response.getString("message");
            showToast(responseMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a toast message on the screen.
     *
     * @param message The message to display.
     */
    private void showToast(String message) {
        Toast.makeText(ActivitySignup.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Validates the format of an email address.
     *
     * @param email The email address to validate.
     * @return True if the email format is valid, otherwise false.
     */
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    /**
     * Validates the format of a password.
     *
     * @param password The password to validate.
     * @return True if the password format is valid, otherwise false.
     */
    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])(?=.{8,}).*$";
        return Pattern.compile(passwordPattern).matcher(password).matches();
    }
}
