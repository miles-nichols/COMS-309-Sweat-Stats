package com.example.fitnesstracker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityDisplayLiftsSearch extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterLift liftAdapter;
    private ArrayList<HashMap<String, String>> liftsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_lifts);

        recyclerView = findViewById(R.id.recycler_view_lifts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the list passed from the previous activity
        liftsList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("lifts");

        // Convert the HashMap list to ObjectLift list
        List<ObjectLift> objectLifts = convertToObjectLiftList(liftsList);

        // Initialize the adapter with the converted list
        liftAdapter = new AdapterLift(objectLifts, lift -> {
            // Handle lift removal action
            objectLifts.remove(lift);
            liftAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Lift removed: " + lift.getTitle(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(liftAdapter);
    }

    /**
     * Converts a list of HashMap<String, String> to a list of ObjectLift.
     */
    private List<ObjectLift> convertToObjectLiftList(ArrayList<HashMap<String, String>> liftsList) {
        List<ObjectLift> objectLifts = new ArrayList<>();
        for (HashMap<String, String> liftMap : liftsList) {
            ObjectLift lift = new ObjectLift(
                    liftMap.get("title"),
                    liftMap.get("description"),
                    liftMap.get("type"),
                    liftMap.get("muscleGroup"),
                    liftMap.get("equipment"),
                    liftMap.get("level")
            );
            objectLifts.add(lift);
        }
        return objectLifts;
    }
}
