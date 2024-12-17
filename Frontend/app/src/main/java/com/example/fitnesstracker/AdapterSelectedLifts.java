package com.example.fitnesstracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for displaying selected lifts in ActivityNewWorkout.
 */
public class AdapterSelectedLifts extends RecyclerView.Adapter<AdapterSelectedLifts.LiftViewHolder> {

    private List<ObjectLift> selectedLifts;
    private OnRemoveLiftListener removeListener;

    public interface OnRemoveLiftListener {
        void onRemoveLift(ObjectLift lift);
    }

    public AdapterSelectedLifts(List<ObjectLift> selectedLifts, OnRemoveLiftListener removeListener) {
        this.selectedLifts = selectedLifts;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public LiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_lift, parent, false);
        return new LiftViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LiftViewHolder holder, int position) {
        ObjectLift lift = selectedLifts.get(position);
        holder.tvLiftTitle.setText(lift.getTitle());

        holder.btnRemoveLift.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onRemoveLift(lift);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedLifts.size();
    }

    public static class LiftViewHolder extends RecyclerView.ViewHolder {
        TextView tvLiftTitle;
        ImageButton btnRemoveLift;

        public LiftViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLiftTitle = itemView.findViewById(R.id.tvLiftTitle);
            btnRemoveLift = itemView.findViewById(R.id.btnRemoveLift);
        }
    }
}
