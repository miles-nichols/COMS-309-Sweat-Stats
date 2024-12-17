package com.example.fitnesstracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for RecyclerView to display sets.
 */
public class AdapterSet extends RecyclerView.Adapter<AdapterSet.SetViewHolder> {

    private final List<ObjectSet> sets;

    /**
     * Constructor for AdapterSet.
     *
     * @param sets List of ObjectSet representing each set.
     */
    public AdapterSet(List<ObjectSet> sets) {
        this.sets = sets;
    }

    @NonNull
    @Override
    public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the set item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_set, parent, false);
        return new SetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {
        ObjectSet currentSet = sets.get(position);
        holder.bind(currentSet);
    }

    @Override
    public int getItemCount() {
        return sets.size();
    }

    /**
     * ViewHolder class for sets.
     */
    static class SetViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvReps;
        private final TextView tvWeight;

        /**
         * Constructor for SetViewHolder.
         *
         * @param itemView The view of the set item.
         */
        public SetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReps = itemView.findViewById(R.id.tvReps);
            tvWeight = itemView.findViewById(R.id.tvWeight);
        }

        /**
         * Binds the data to the UI elements.
         *
         * @param set The ObjectSet to bind.
         */
        public void bind(ObjectSet set) {
            tvReps.setText("Reps: " + set.getReps());
            tvWeight.setText("Weight: " + set.getWeight() + " kg");
        }
    }
}
