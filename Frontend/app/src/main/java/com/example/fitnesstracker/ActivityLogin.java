package com.example.fitnesstracker;

import static androidx.compose.ui.semantics.SemanticsPropertiesKt.setText;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity that handles user login functionality in the fitness tracker application.
 * This activity allows the user to input their username and password, and then attempts to log in
 * by making a POST request to the backend. If login is successful, the user is redirected to the home screen.
 *
 * @author Miles Nichols
 */
public class ActivityLogin extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;
    private static String USERNAME;
    private static String PASSWORD;
    private static String role;

    private String url = "http://coms-3090-014.class.las.iastate.edu:8080/user/login";

    /**
     * Initializes the login activity, sets up the UI elements and listeners for login and sign-up buttons.
     * @param savedInstanceState The saved instance state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.login_username_edt);
        passwordEditText = findViewById(R.id.login_password_edt);
        loginButton = findViewById(R.id.login_login_btn);
        signupButton = findViewById(R.id.login_signup_btn);

        usernameEditText.setText("");
        passwordEditText.setText("");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                USERNAME = username;
                PASSWORD = password;
                fetchUserRole(username, password);
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLogin.this, ActivitySignup.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Makes a POST request to the backend to authenticate the user with the provided username and password.
     * If the login is successful, the user is redirected to the home screen.
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     */
    private void makeJsonArrayReq(String username, String password) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String responseMessage = response.getString("message");
                            if(responseMessage.equals("Login successful")) {
                                Intent intent = new Intent(ActivityLogin.this, ActivityHomeScreen.class);
                                startActivity(intent);
                            } else if (responseMessage.equals("Incorrect password")) {
                                Toast.makeText(ActivityLogin.this, "Login failed.", Toast.LENGTH_SHORT).show();
                            } else if (responseMessage.equals("User not found with username: " + USERNAME)) {
                                Toast.makeText(ActivityLogin.this, "This account does not exist, please make an account!", Toast.LENGTH_SHORT).show();
                            }
                            else if(responseMessage.equals("Too many strikes")){
                                Toast.makeText(ActivityLogin.this, "This account has been banned!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(ActivityLogin.this, "Error occurred, please try again later", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Retrieves the username entered by the user.
     * @return The username as a string.
     */
    public static String getUsername() {
        return USERNAME;
    }

    /**
     * Retrieves the password entered by the user.
     * @return The password as a string.
     */
    public static String getPassword() {
        return PASSWORD;
    }

    public void setUsername(String username) {
        USERNAME = username;
    }

    private void fetchUserRole(final String username, final String password) {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/user/role?username=" + username;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    String role = response.trim().replace("\"", "").toUpperCase();
                    handleUserRole(role, username, password);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to fetch role", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }




    private void handleUserRole(String role, String username, String password) {
        Log.d("Role", "Fetched role: " + role);  // Log the role for debugging

        switch (role) {
            case "ADMIN":
            case "TRAINER":
            case "USER":
                setRole(role);  // Save role
                makeJsonArrayReq(username, password);  // Proceed with login
                break;
            default:
                Log.e("Role Error", "Unknown role: " + role);  // Log the unknown role
                Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
        }
    }



    public static String getUserLevel() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
