package com.example.fitnesstracker;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * ActivityChat is an activity that enables real-time chat between users using WebSocket.
 * It allows users to send and receive messages in a chat interface.
 * @author Miles Nichols
 */
public class ActivityChat extends AppCompatActivity {

    private WebSocket webSocket;
    private EditText messageEditText;
    private LinearLayout chatHistoryContainer;
    private String currentUsername;
    private String friendName;
    private OkHttpClient client;
    private static final String BASE_URL = "ws://coms-3090-014.class.las.iastate.edu:8080/chat/";

    /**
     * Called when the activity is first created.
     * Initializes UI components, retrieves necessary data, and sets up WebSocket connection.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState().
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentUsername = ActivityLogin.getUsername();
        friendName = getIntent().getStringExtra("friend_username");

        messageEditText = findViewById(R.id.messageEditText);
        chatHistoryContainer = findViewById(R.id.chatHistoryContainer);
        ImageButton sendButton = findViewById(R.id.sendButton);
        client = new OkHttpClient();
        initializeWebSocket();

        sendButton.setOnClickListener(view -> {
            String message = messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage("@" + friendName + " " + message);
                messageEditText.setText("");
            } else {
                Toast.makeText(this, "Please enter a message.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Initializes the WebSocket connection with the server using the current username.
     */
    private void initializeWebSocket() {
        String url = BASE_URL + currentUsername;
        Request request = new Request.Builder().url(url).build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                runOnUiThread(() -> Toast.makeText(ActivityChat.this, "Connected to server", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                runOnUiThread(() -> receiveMessage(text));
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                runOnUiThread(() -> receiveMessage(bytes.utf8()));
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                runOnUiThread(() -> Toast.makeText(ActivityChat.this, "Disconnected: " + reason, Toast.LENGTH_SHORT).show());
                webSocket.close(1000, null);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                runOnUiThread(() -> Toast.makeText(ActivityChat.this, "WebSocket Error: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * Sends a message to the WebSocket server and updates the chat history in the UI.
     *
     * @param message The message to be sent to the server.
     */
    private void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
            TextView sentMessageView = new TextView(this);
            sentMessageView.setText("You: " + message);
            chatHistoryContainer.addView(sentMessageView);
        } else {
            Toast.makeText(this, "WebSocket is not connected", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Displays a received message in the chat history container.
     *
     * @param message The message received from the WebSocket server.
     */
    private void receiveMessage(String message) {
        TextView messageView = new TextView(this);
        messageView.setText("Friend: " + message);
        chatHistoryContainer.addView(messageView);
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
}
