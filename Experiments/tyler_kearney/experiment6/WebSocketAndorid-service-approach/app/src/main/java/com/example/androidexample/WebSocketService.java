package com.example.androidexample;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.os.Looper;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class WebSocketService extends Service {

    private final Map<String, WebSocketClient> webSockets = new HashMap<>();
    private static final int RECONNECT_INTERVAL_MS = 5000; // Retry every 5 seconds

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("CONNECT".equals(action)) {
                String url = intent.getStringExtra("url");
                String key = intent.getStringExtra("key");
                connectWebSocket(key, url);
            } else if ("DISCONNECT".equals(action)) {
                String key = intent.getStringExtra("key");
                disconnectWebSocket(key);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("SendWebSocketMessage"));
    }

    private void connectWebSocket(String key, String url) {
        try {
            URI serverUri = URI.create(url);
            WebSocketClient webSocketClient = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d("WebSocket", "Connected to " + key);
                    broadcastStatus("Connected to " + key);
                }

                @Override
                public void onMessage(String message) {
                    Intent intent = new Intent("WebSocketMessageReceived");
                    intent.putExtra("key", key);
                    intent.putExtra("message", message);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("WebSocket", "Disconnected from " + key);
                    broadcastStatus("Disconnected from " + key);
                    reconnectWebSocket(key, url); // Optional: attempt to reconnect
                }

                @Override
                public void onError(Exception ex) {
                    Log.e("WebSocket", "Error with " + key + ": " + ex.getMessage());
                    broadcastStatus("Error with " + key);
                    reconnectWebSocket(key, url); // Optional: attempt to reconnect
                }
            };

            webSocketClient.connect();
            webSockets.put(key, webSocketClient);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disconnectWebSocket(String key) {
        if (webSockets.containsKey(key)) {
            webSockets.get(key).close();
        }
    }

    // Helper method to broadcast connection status
    private void broadcastStatus(String status) {
        Intent intent = new Intent("ConnectionStatusUpdate");
        intent.putExtra("status", status);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (WebSocketClient client : webSockets.values()) {
            client.close();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = intent.getStringExtra("key");
            String message = intent.getStringExtra("message");

            WebSocketClient webSocket = webSockets.get(key);
            if (webSocket != null) {
                webSocket.send(message);
            }
        }
    };

    private void reconnectWebSocket(String key, String url) {
        new Handler().postDelayed(() -> connectWebSocket(key, url), RECONNECT_INTERVAL_MS);
    }
}
