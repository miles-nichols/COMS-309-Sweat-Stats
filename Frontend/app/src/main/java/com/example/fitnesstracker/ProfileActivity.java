// ProfileActivity.java
package com.example.fitnesstracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://coms-3090-014.class.las.iastate.edu:8080/";

    private TextView maxBenchTextView, maxSquatTextView, maxDeadliftTextView, maxCleanTextView, lastLiftTextView, streakTextView;
    private EditText displayNameEditText, bioEditText, favoriteLiftEditText;
    private ImageView profileImageView;
    private Button editProfileButton, saveButton, changeProfilePicButton;
    private ImageButton btnBack;


    private boolean bioEdited = false, displayNameEdited = false, favoriteLiftEdited = false, profilePictureEdited = false;
    private RequestQueue requestQueue;
    private Bitmap selectedProfileImage;

    private final ActivityResultLauncher<Intent> selectPictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    try {
                        selectedProfileImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(result.getData().getData()));
                        profileImageView.setImageBitmap(selectedProfileImage);
                        profilePictureEdited = true;
                    } catch (Exception e) {
                        Log.e("ProfileActivity", "Error loading selected image", e);
                    }
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Ensure this layout is correct

        initializeUIComponents();
        requestQueue = Volley.newRequestQueue(this);

        String currentUsername = ActivityLogin.getUsername(); // Ensure this method retrieves the correct username
        if (currentUsername == null || currentUsername.isEmpty()) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            // Redirect to login or handle appropriately
            Intent intent = new Intent(this, ActivityLogin.class);
            startActivity(intent);
            finish();
            return;
        }

        fetchProfileData(currentUsername);
        setupButtonListeners(currentUsername);
        fetchStreak(currentUsername);
    }

    private void initializeUIComponents() {
        displayNameEditText = findViewById(R.id.displayNameEditText);
        bioEditText = findViewById(R.id.bioEditText);
        favoriteLiftEditText = findViewById(R.id.favoriteLiftEditText);
        maxBenchTextView = findViewById(R.id.maxBenchTextView);
        maxSquatTextView = findViewById(R.id.maxSquatTextView);
        maxDeadliftTextView = findViewById(R.id.maxDeadliftTextView);
        maxCleanTextView = findViewById(R.id.maxCleanTextView);
        lastLiftTextView = findViewById(R.id.lastLiftTextView);
        streakTextView = findViewById(R.id.streakTextView);
        profileImageView = findViewById(R.id.profileImageView);
        btnBack = findViewById(R.id.btnBack);
        editProfileButton = findViewById(R.id.editProfileButton);
        saveButton = findViewById(R.id.saveButton);
        changeProfilePicButton = findViewById(R.id.changeProfilePicButton);

        saveButton.setVisibility(View.GONE);
        changeProfilePicButton.setVisibility(View.GONE);
    }

    private Bitmap resizeImage(Bitmap bitmap) {
        int maxWidth = 500; // Maximum width in pixels
        int maxHeight = 500; // Maximum height in pixels
        return Bitmap.createScaledBitmap(bitmap, maxWidth, maxHeight, true);
    }

    private void setupButtonListeners(String username) {
        editProfileButton.setOnClickListener(v -> toggleEditMode(true));

        saveButton.setOnClickListener(v -> {
            if (displayNameEdited || bioEdited || favoriteLiftEdited) saveProfileData(username);
            if (profilePictureEdited) uploadProfilePicture(username, selectedProfileImage);
            toggleEditMode(false);
            resetEditFlags();
        });

        changeProfilePicButton.setOnClickListener(v -> selectProfilePicture());

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityHomeScreen.class);
            startActivity(intent);
            finish();
        });

        displayNameEditText.setOnFocusChangeListener((v, hasFocus) -> { if (!hasFocus) displayNameEdited = true; });
        bioEditText.setOnFocusChangeListener((v, hasFocus) -> { if (!hasFocus) bioEdited = true; });
        favoriteLiftEditText.setOnFocusChangeListener((v, hasFocus) -> { if (!hasFocus) favoriteLiftEdited = true; });
    }

    private void toggleEditMode(boolean enable) {
        displayNameEditText.setEnabled(enable);
        bioEditText.setEnabled(enable);
        favoriteLiftEditText.setEnabled(enable);
        saveButton.setVisibility(enable ? View.VISIBLE : View.GONE);
        changeProfilePicButton.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    private void resetEditFlags() {
        bioEdited = false;
        displayNameEdited = false;
        favoriteLiftEdited = false;
        profilePictureEdited = false;
    }

    private void selectProfilePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        selectPictureLauncher.launch(intent);
    }

    private void fetchProfileData(String username) {
        if (username == null || username.isEmpty()) {
            Log.e("ProfileActivity", "Username is invalid or empty. Cannot fetch profile data.");
            Toast.makeText(this, "Invalid username. Cannot fetch profile data.", Toast.LENGTH_SHORT).show();
            return;
        }

        String profileUrl = BASE_URL + "user/profile/" + username;
        String pictureUrl = BASE_URL + "user/picture/" + username;

        // Fetch user profile information (bio, favorite lift, etc.)
        JsonObjectRequest profileRequest = new JsonObjectRequest(Request.Method.GET, profileUrl, null,
                response -> {
                    // Set profile details
                    displayNameEditText.setText(response.optString("name", ""));
                    bioEditText.setText(response.optString("bio", ""));
                    favoriteLiftEditText.setText(response.optString("favLiftName", ""));

                    // Fetch and set maxes
                    fetchAllMaxes(username);

                    // Fetch profile picture
                    fetchProfilePicture(pictureUrl);

                    // Fetch last workout details
                    fetchLastWorkout(username);
                },
                error -> {
                    String errorMessage = error.networkResponse != null ? new String(error.networkResponse.data) : "Unknown error";
                    Log.e("ProfileActivity", "Error fetching profile: " + errorMessage, error);
                    Toast.makeText(this, "Error fetching profile: " + errorMessage, Toast.LENGTH_LONG).show();
                });

        requestQueue.add(profileRequest);
    }


    private void fetchAllMaxes(String username) {
        // Define the list of lifts and their labels
        Map<String, TextView> lifts = new HashMap<>();
        lifts.put("Bench", maxBenchTextView);
        lifts.put("Squat", maxSquatTextView);
        lifts.put("Deadlift", maxDeadliftTextView);
        lifts.put("Clean", maxCleanTextView);

        // Iterate through each lift and fetch its max
        for (Map.Entry<String, TextView> entry : lifts.entrySet()) {
            String liftTitle = entry.getKey();
            TextView targetTextView = entry.getValue();
            fetchMaxForLift(username, liftTitle, targetTextView);
        }
    }

    private void fetchMaxForLift(String username, String liftTitle, TextView targetTextView) {
        if (username == null || username.isEmpty() || liftTitle == null || liftTitle.isEmpty()) {
            Log.e("ProfileActivity", "Invalid parameters for fetching maxes.");
            return;
        }

        String url = BASE_URL + "user/maxes/" + username + "/" + liftTitle;

        JsonObjectRequest maxRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("ProfileActivity", "Max response for " + liftTitle + ": " + response.toString());
                    try {
                        if (response.has("maxWeight")) {
                            int maxLift = response.getInt("maxWeight");
                            targetTextView.setText(liftTitle + ": " + maxLift + " lbs");
                        } else {
                            Log.e("ProfileActivity", "Response missing 'max' field for " + liftTitle);
                            targetTextView.setText(liftTitle + ": N/A");
                        }
                    } catch (JSONException e) {
                        Log.e("ProfileActivity", "Error parsing maxes response for " + liftTitle, e);
                        targetTextView.setText(liftTitle + ": N/A");
                    }
                },
                error -> {
                    String errorMessage = "Unknown error";
                    if (error.networkResponse != null) {
                        errorMessage = "Status Code: " + error.networkResponse.statusCode + ", Response: " + new String(error.networkResponse.data);
                    }
                    Log.e("ProfileActivity", "Error fetching max for " + liftTitle + ": " + errorMessage, error);
                    Toast.makeText(this, "Failed to fetch max " + liftTitle + ".", Toast.LENGTH_SHORT).show();
                    targetTextView.setText(liftTitle + ": N/A");
                });

        requestQueue.add(maxRequest);
    }


    private void fetchStreak(String username) {
        String url = BASE_URL + "user/streak/" + username;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> streakTextView.setText("Streak: " + response.trim() + " days"),
                error -> {
                    String errorMessage = error.networkResponse != null
                            ? "Server Error: " + error.networkResponse.statusCode
                            : "Network Error";
                    Log.e("ProfileActivity", "Error fetching streak: " + errorMessage, error);
                    Toast.makeText(this, "Failed to fetch workout streak.", Toast.LENGTH_SHORT).show();
                    streakTextView.setText("Streak: N/A");
                });
        requestQueue.add(request);
    }

    private void fetchLastWorkout(String username) {
        String url = BASE_URL + "user/lastWorkout/" + username;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    String completedWorkoutName = response.optString("name", "N/A");
                    String originalWorkoutName = extractOriginalWorkoutName(completedWorkoutName);
                    String workoutDate = response.optString("date", "N/A");

                    // Set the text in the desired format
                    String lastWorkoutText = "Last Workout Completed was: " + originalWorkoutName + " on " + workoutDate;
                    lastLiftTextView.setText(lastWorkoutText);
                },
                error -> {
                    if (error.networkResponse != null) {
                        String errorData = new String(error.networkResponse.data);
                        Log.e("ProfileActivity", "Error response from server: " + errorData);
                    }
                    String errorMessage = error.networkResponse != null
                            ? "Server Error: " + error.networkResponse.statusCode
                            : "Network Error";
                    Log.e("ProfileActivity", "Error fetching last workout: " + errorMessage, error);
                    Toast.makeText(this, "Error fetching last workout", Toast.LENGTH_SHORT).show();
                    lastLiftTextView.setText("Last Workout Completed was: \"N/A\" on \"N/A\"");
                });
        requestQueue.add(request);
    }

    /**
     * Extracts the original workout name by removing the "_completed_<timestamp>" suffix.
     *
     * @param completedWorkoutName The name of the completed workout (e.g., "ass_completed_20241209200620").
     * @return The original workout name (e.g., "ass").
     */
    private String extractOriginalWorkoutName(String completedWorkoutName) {
        // Assuming the format is "workoutName_completed_timestamp"
        if (completedWorkoutName.contains("_completed_")) {
            return completedWorkoutName.split("_completed_")[0];
        }
        return completedWorkoutName; // Return as is if the pattern doesn't match
    }

    private void fetchProfilePicture(String pictureUrl) {
        if (pictureUrl == null || pictureUrl.isEmpty()) {
            Log.e("ProfileActivity", "Picture URL is invalid or empty.");
            Toast.makeText(this, "Invalid picture URL. Cannot fetch profile picture.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("ProfileActivity", "Fetching profile picture from URL: " + pictureUrl);

        ImageRequest pictureRequest = new ImageRequest(pictureUrl,
                response -> {
                    // Crop the bitmap into a circle
                    Bitmap circularBitmap = getCircularBitmap(response);
                    profileImageView.setImageBitmap(circularBitmap);
                },
                0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                error -> {
                    String errorMessage = error.networkResponse != null
                            ? "Server Error: " + error.networkResponse.statusCode
                            : "Network Error";
                    Log.e("ProfileActivity", "Error loading profile picture: " + errorMessage, error);
                    Toast.makeText(this, "Failed to load profile picture.", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(pictureRequest);
    }


    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        int left = (bitmap.getWidth() - size) / 2;
        int top = (bitmap.getHeight() - size) / 2;

        RectF rect = new RectF(0, 0, size, size);
        Rect src = new Rect(left, top, left + size, top + size);

        canvas.drawOval(rect, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, rect, paint);

        return output;
    }


    private void saveProfileData(String username) {
        String url = BASE_URL + "user/profile/" + username;
        JSONObject requestBody = new JSONObject();


        try {
            requestBody.put("name", displayNameEditText.getText().toString().trim());
            requestBody.put("bio", bioEditText.getText().toString().trim());
            requestBody.put("favLiftName", favoriteLiftEditText.getText().toString().trim());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, requestBody,
                    response -> Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show(),
                    error -> {
                        String errorMsg = "Unknown error";
                        if (error.networkResponse != null) {
                            errorMsg = new String(error.networkResponse.data);
                        }
                        Log.e("ProfileActivity", "Error saving profile: " + errorMsg, error);
                        Toast.makeText(this, "Error saving profile: " + errorMsg, Toast.LENGTH_LONG).show();
                    });

            requestQueue.add(request);
        } catch (JSONException e) {
            Log.e("ProfileActivity", "Error creating JSON request body", e);
        }
    }

    private void uploadProfilePicture(String username, Bitmap bitmap) {
        new UploadImageTask(username, bitmap).execute();
    }

    private class UploadImageTask extends AsyncTask<Void, Void, Boolean> {
        private String username;
        private Bitmap bitmap;

        UploadImageTask(String username, Bitmap bitmap) {
            this.username = username;
            this.bitmap = bitmap;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String urlString = BASE_URL + "user/picture/" + username;
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"profile.jpg\"" + lineEnd);
                dos.writeBytes("Content-Type: image/jpeg" + lineEnd);
                dos.writeBytes(lineEnd);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] imageData = baos.toByteArray();
                dos.write(imageData);

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                dos.flush();
                dos.close();

                int responseCode = conn.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                Log.e("ProfileActivity", "Error uploading image", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(ProfileActivity.this, "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Error uploading profile picture", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
