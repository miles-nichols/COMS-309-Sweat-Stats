package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FunActivity extends AppCompatActivity {

    private TextView mainTxt; // define number textview variable
    private Button backBtn;     // define back button variable


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fun);

        /* initialize UI elements */
        mainTxt = findViewById(R.id.maintext);
        backBtn = findViewById(R.id.back);

        mainTxt.setText("You lose?");

        /* when back btn is pressed, switch back to MainActivity */
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}