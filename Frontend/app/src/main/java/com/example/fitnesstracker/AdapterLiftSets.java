package com.example.fitnesstracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterLiftSets extends RecyclerView.Adapter<AdapterLiftSets.LiftViewHolder> {

    private List<ObjectLiftSets> liftSetsList;

    public AdapterLiftSets(List<ObjectLiftSets> liftSetsList) {
        this.liftSetsList = liftSetsList;
    }

    @Override
    public LiftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_lift_item, parent, false);
        return new LiftViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LiftViewHolder holder, int position) {
        ObjectLiftSets liftSet = liftSetsList.get(position);
        holder.liftName.setText(liftSet.getName());

        StringBuilder setsString = new StringBuilder();
        for (ObjectSet set : liftSet.getSets()) {
            setsString.append("Reps: ").append(set.getReps()).append(", Weight: ").append(set.getWeight()).append("\n");
        }
        holder.liftSets.setText(setsString.toString());
    }

    @Override
    public int getItemCount() {
        return liftSetsList.size();
    }

    public static class LiftViewHolder extends RecyclerView.ViewHolder {

        public TextView liftName;
        public TextView liftSets;

        public LiftViewHolder(View view) {
            super(view);
            liftName = view.findViewById(R.id.lift_name);
            liftSets = view.findViewById(R.id.lift_sets);
        }
    }
}
