package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Activity class representing the Social Feed in the Fitness Tracker app.
 * Displays a feed of messages or posts and allows navigation to other parts of the app.
 * Utilizes WebSocket for real-time updates.
 *
 * @author Tyler Kearney
 */
public class SocialFeedActivity extends AppCompatActivity {

    private ListView feedListView;
    private FeedAdapter feedAdapter;
    private ArrayList<FeedItem> feedItems = new ArrayList<>();
    private static OkHttpClient client;  // Singleton OkHttpClient
    private WebSocket webSocket;
    private static final String BASE_URL = "ws://coms-3090-014.class.las.iastate.edu:8080/ws"; // WebSocket endpoint

    // Navigation bar buttons
    private ImageButton btnProfile;
    private ImageButton btnFriends;
    private ImageButton btnLiftSearch;
    private ImageButton btnCalendar;
    private ImageButton btnSettings;

    /**
     * Initializes the activity and sets up the UI and WebSocket connection.
     *
     * @param savedInstanceState Contains the most recent data, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_feed);

        // Ensure OkHttpClient is initialized only once
        if (client == null) {
            client = new OkHttpClient();
        }

        setupUI();
        connectWebSocket();
    }

    /**
     * Configures the UI components, including ListView for the feed and navigation buttons.
     */
    private void setupUI() {
        feedListView = findViewById(R.id.feedListView);
        feedAdapter = new FeedAdapter(this, feedItems);
        feedListView.setAdapter(feedAdapter);

        // Initialize navigation buttons
        btnProfile = findViewById(R.id.btnProfile);
        btnFriends = findViewById(R.id.btnFriends);
        btnLiftSearch = findViewById(R.id.btnLiftSearch);
        btnCalendar = findViewById(R.id.btnCalendar);
        btnSettings = findViewById(R.id.btnSettings);

        // Set up click listeners for navigation buttons
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SocialFeedActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnFriends.setOnClickListener(v -> {
            Intent intent = new Intent(SocialFeedActivity.this, ActivityFriends.class);
            startActivity(intent);
        });

        btnLiftSearch.setOnClickListener(v -> {
            Intent intent = new Intent(SocialFeedActivity.this, ActivityLiftSearch.class);
            startActivity(intent);
        });

        btnCalendar.setOnClickListener(v -> {
            // Uncomment and implement CalendarActivity if it exists
            // Intent intent = new Intent(SocialFeedActivity.this, CalendarActivity.class);
            // startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            // Uncomment and implement SettingsActivity if it exists
            // Intent intent = new Intent(SocialFeedActivity.this, SettingsActivity.class);
            // startActivity(intent);
        });
    }

    /**
     * Establishes a WebSocket connection to the server for real-time feed updates.
     */
    private void connectWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, null);  // Close any existing WebSocket
        }
        Request request = new Request.Builder().url(BASE_URL).build();
        webSocket = client.newWebSocket(request, new FeedWebSocketListener());
    }

    /**
     * Closes the WebSocket connection when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Activity destroyed");
        }
    }

    /**
     * WebSocketListener class to handle WebSocket events such as messages, errors, and closures.
     */
    private class FeedWebSocketListener extends WebSocketListener {

        /**
         * Called when the WebSocket connection is successfully opened.
         *
         * @param webSocket The active WebSocket connection.
         * @param response The server's response to the connection.
         */
        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
            Log.d("WebSocket", "Connected to WebSocket");
        }

        /**
         * Called when a text message is received from the WebSocket.
         * Updates the feed with the received message.
         *
         * @param webSocket The active WebSocket connection.
         * @param text The received text message.
         */
        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            runOnUiThread(() -> {
                Log.d("WebSocket", "Received message: " + text);
                updateFeedWithMessage(text);
            });
        }

        /**
         * Called when binary data is received (not currently used).
         *
         * @param webSocket The active WebSocket connection.
         * @param bytes The received binary data.
         */
        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
            Log.d("WebSocket", "Received bytes: " + bytes.hex());
        }

        /**
         * Called when the WebSocket is closing.
         *
         * @param webSocket The active WebSocket connection.
         * @param code The status code of the closure.
         * @param reason The reason for closure.
         */
        @Override
        public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            Log.d("WebSocket", "Closing: " + code + "/" + reason);
            webSocket.close(1000, null);
        }

        /**
         * Called when a WebSocket connection failure occurs.
         *
         * @param webSocket The active WebSocket connection.
         * @param t The throwable error causing the failure.
         * @param response The server's response, if any.
         */
        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable okhttp3.Response response) {
            Log.e("WebSocket", "Error: " + t.getMessage(), t);
            runOnUiThread(() -> Toast.makeText(SocialFeedActivity.this, "WebSocket connection failed", Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * Updates the feed with a new message received from the WebSocket.
     *
     * @param message The message to add to the feed.
     */
    private void updateFeedWithMessage(String message) {
        // Add the received message directly as a FeedItem and update the adapter
        FeedItem newItem = new FeedItem(message);  // Adjusted FeedItem to take a single message string
        feedItems.add(0, newItem); // Add to top of list
        feedAdapter.notifyDataSetChanged();
    }
}
