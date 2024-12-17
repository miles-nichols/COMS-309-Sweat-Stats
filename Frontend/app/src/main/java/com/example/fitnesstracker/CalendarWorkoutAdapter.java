package com.example.fitnesstracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarWorkoutAdapter extends RecyclerView.Adapter<CalendarWorkoutAdapter.WorkoutViewHolder> {

    private Context context;
    private List<ObjectActiveWorkout> workoutList;

    public CalendarWorkoutAdapter(Context context, List<ObjectActiveWorkout> workoutList) {
        this.context = context;
        this.workoutList = workoutList;
    }

    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WorkoutViewHolder holder, int position) {
        ObjectActiveWorkout workout = workoutList.get(position);
        holder.workoutName.setText(workout.getName());
        StringBuilder detailsBuilder = new StringBuilder();
        for (ObjectLiftSets liftSet : workout.getLiftSets()) {
            detailsBuilder.append("Lift: ").append(liftSet.getName()).append("\n");
            List<ObjectSet> sets = liftSet.getSets();
            if (sets != null && !sets.isEmpty()) {
                for (int i = 0; i < sets.size(); i++) {
                    ObjectSet set = sets.get(i);
                    detailsBuilder.append("  Set ").append(i + 1).append(": ")
                            .append(set.getReps()).append(" reps @ ")
                            .append(set.getWeight()).append(" kg\n");
                }
            } else {
                detailsBuilder.append("  No sets recorded.\n");
            }
        }
        holder.workoutDetails.setText(detailsBuilder.toString().trim());
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }
    private String formatDate(Date date) {
        if (date == null) return "Unknown Date";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView workoutName;
        TextView workoutDetails;

        public WorkoutViewHolder(View itemView) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.tvWorkoutName);
            workoutDetails = itemView.findViewById(R.id.workout_details);
        }
    }

}
