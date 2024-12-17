package com.example.fitnesstracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for displaying a list of workouts in a RecyclerView.
 * This adapter binds workout data to a TextView in the list item layout and handles
 * the click event on a workout item. It optionally handles workout deletion.
 */
public class AdapterWorkout extends RecyclerView.Adapter<AdapterWorkout.ViewHolder> {

    /**
     * Callback interface for handling workout item clicks.
     */
    public interface WorkoutClickCallback {
        void onWorkoutClicked(ObjectWorkout workout);
    }

    /**
     * Callback interface for handling workout deletions.
     */
    public interface RemoveWorkoutCallback {
        void onRemoveWorkout(ObjectWorkout workout);
    }

    private Context context;
    private List<ObjectWorkout> workoutList;
    private WorkoutClickCallback clickCallback;
    private RemoveWorkoutCallback removeCallback;

    /**
     * Constructor for AdapterWorkout with delete functionality.
     *
     * @param context        The context in which the adapter is used.
     * @param workoutList    The list of workouts to display.
     * @param clickCallback  The callback to handle workout clicks.
     * @param removeCallback The callback to handle workout deletions.
     */
    public AdapterWorkout(Context context, List<ObjectWorkout> workoutList, WorkoutClickCallback clickCallback, RemoveWorkoutCallback removeCallback) {
        this.context = context;
        this.workoutList = workoutList;
        this.clickCallback = clickCallback;
        this.removeCallback = removeCallback;
    }

    /**
     * Constructor for AdapterWorkout without delete functionality.
     *
     * @param context       The context in which the adapter is used.
     * @param workoutList   The list of workouts to display.
     * @param clickCallback The callback to handle workout clicks.
     */
    public AdapterWorkout(Context context, List<ObjectWorkout> workoutList, WorkoutClickCallback clickCallback) {
        this(context, workoutList, clickCallback, null);
    }

    @NonNull
    @Override
    public AdapterWorkout.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_workout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterWorkout.ViewHolder holder, int position) {
        ObjectWorkout workout = workoutList.get(position);
        holder.tvWorkoutName.setText(workout.getName());

        holder.itemView.setOnClickListener(v -> clickCallback.onWorkoutClicked(workout));

        if (removeCallback != null) {
            holder.btnRemoveWorkout.setVisibility(View.VISIBLE);
            holder.btnRemoveWorkout.setOnClickListener(v -> removeCallback.onRemoveWorkout(workout));
        } else {
            holder.btnRemoveWorkout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    /**
     * ViewHolder class for holding workout item views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWorkoutName;
        ImageButton btnRemoveWorkout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWorkoutName = itemView.findViewById(R.id.tvWorkoutName);
            btnRemoveWorkout = itemView.findViewById(R.id.btnRemoveWorkout);
        }
    }
}
