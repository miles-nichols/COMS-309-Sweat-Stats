package com.example.fitnesstracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterWorkoutDetails extends RecyclerView.Adapter<AdapterWorkoutDetails.WorkoutViewHolder> {

    private List<ObjectActiveWorkout> workoutList;

    public AdapterWorkoutDetails(List<ObjectActiveWorkout> workoutList) {
        this.workoutList = workoutList;
    }

    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WorkoutViewHolder holder, int position) {
        ObjectActiveWorkout workout = workoutList.get(position);

        holder.workoutName.setText(workout.getName());
        holder.workoutDate.setText(workout.getDate().toString());
        holder.workoutDetails.setText(workout.getLiftSets().toString()); // Modify as needed
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView workoutName;
        TextView workoutDate;
        TextView workoutDetails;

        public WorkoutViewHolder(View itemView) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.workout_name);
            workoutDate = itemView.findViewById(R.id.workout_date);
            workoutDetails = itemView.findViewById(R.id.workout_details);
        }
    }
}
