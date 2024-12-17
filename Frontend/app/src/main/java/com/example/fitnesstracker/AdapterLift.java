package com.example.fitnesstracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterLift extends RecyclerView.Adapter<AdapterLift.LiftViewHolder> {

    private List<ObjectLift> liftList;
    private OnLiftRemoveListener removeListener;

    // Listener interface for lift removal
    public interface OnLiftRemoveListener {
        void onRemove(ObjectLift lift);
    }

    public AdapterLift(List<ObjectLift> liftList, OnLiftRemoveListener removeListener) {
        this.liftList = liftList;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public LiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lift, parent, false);
        return new LiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LiftViewHolder holder, int position) {
        ObjectLift lift = liftList.get(position);

        // Set the lift title
        holder.tvLiftTitle.setText(lift.getTitle());

        // Remove lift when the "X" is clicked
        holder.btnRemoveLift.setOnClickListener(v -> {
            removeListener.onRemove(lift);
            liftList.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return liftList.size();
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
