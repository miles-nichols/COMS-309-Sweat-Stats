// PresetWorkoutAdapter.java
package com.example.fitnesstracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PresetWorkoutAdapter extends RecyclerView.Adapter<PresetWorkoutAdapter.ViewHolder> {

    // Interface for callback when a workout is selected
    public interface OnWorkoutSelectedListener {
        void onWorkoutSelected(ObjectWorkout workout);
    }

    private List<ObjectWorkout> workouts;
    private OnWorkoutSelectedListener listener;

    // Constructor
    public PresetWorkoutAdapter(List<ObjectWorkout> workouts, OnWorkoutSelectedListener listener) {
        this.workouts = workouts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preset_workout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ObjectWorkout workout = workouts.get(position);
        holder.bind(workout, listener);
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView workoutName, workoutDescription;
        Button scheduleButton;

        public ViewHolder(View itemView) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.workoutNameTextView);
            workoutDescription = itemView.findViewById(R.id.workoutDescriptionTextView);
            scheduleButton = itemView.findViewById(R.id.scheduleWorkoutButton);
        }

        public void bind(final ObjectWorkout workout, final OnWorkoutSelectedListener listener) {
            workoutName.setText(workout.getName());
            workoutDescription.setText(workout.getDescription());

            scheduleButton.setOnClickListener(v -> listener.onWorkoutSelected(workout));
        }
    }
}
