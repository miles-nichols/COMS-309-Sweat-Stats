package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;


public class SocialHubActivity extends AppCompatActivity {

    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_hub);

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityHomeScreen.class);
            startActivity(intent);
            finish();
        });

        // Set up buttons for navigating to different activities
//        findViewById(R.id.btnSocialFeed).setOnClickListener(v ->
//                startActivity(new Intent(this, SocialFeedActivity.class)));

        findViewById(R.id.btnActivityFriends).setOnClickListener(v ->
                startActivity(new Intent(this, ActivityFriends.class)));

        findViewById(R.id.btnTrainingCommunity).setOnClickListener(v ->
                startActivity(new Intent(this, ActivityTrainingCommunity.class)));
    }
}
