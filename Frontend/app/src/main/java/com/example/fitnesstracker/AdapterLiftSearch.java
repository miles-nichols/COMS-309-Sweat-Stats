package com.example.fitnesstracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class AdapterLiftSearch extends RecyclerView.Adapter<AdapterLiftSearch.LiftViewHolder> {

    private List<HashMap<String, String>> liftList;
    private Context context;

    private OnAddLiftClickListener addLiftListener;
    private OnViewDetailsClickListener viewDetailsListener;

    public AdapterLiftSearch(List<HashMap<String, String>> liftList,
                             OnAddLiftClickListener addLiftListener,
                             OnViewDetailsClickListener viewDetailsListener,
                             Context context) {
        this.liftList = liftList;
        this.addLiftListener = addLiftListener;
        this.viewDetailsListener = viewDetailsListener;
        this.context = context;
    }

    @NonNull
    @Override
    public LiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lift_search, parent, false);
        return new LiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LiftViewHolder holder, int position) {
        HashMap<String, String> lift = liftList.get(position);
        holder.tvLiftTitle.setText(lift.get("title"));

        // Clicking the entire item adds the lift to the workout
        holder.itemView.setOnClickListener(v -> {
            if (addLiftListener != null) {
                addLiftListener.onAddLiftClicked(lift);
            }
        });

        // Clicking the "View Details" button opens the details screen
        holder.btnViewDetails.setOnClickListener(v -> {
            if (viewDetailsListener != null) {
                viewDetailsListener.onViewDetailsClicked(lift);
            }
        });
    }

    @Override
    public int getItemCount() {
        return liftList.size();
    }

    public static class LiftViewHolder extends RecyclerView.ViewHolder {
        TextView tvLiftTitle;
        ImageButton btnViewDetails; // Use ImageButton here

        public LiftViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLiftTitle = itemView.findViewById(R.id.tvLiftTitle);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails); // No casting to Button, it returns an ImageButton
        }
    }


    public interface OnAddLiftClickListener {
        void onAddLiftClicked(HashMap<String, String> lift);
    }

    public interface OnViewDetailsClickListener {
        void onViewDetailsClicked(HashMap<String, String> lift);
    }
}
