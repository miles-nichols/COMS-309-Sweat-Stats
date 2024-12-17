// AdapterTrainingCommunity.java
package com.example.fitnesstracker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class AdapterTrainingCommunity extends RecyclerView.Adapter<AdapterTrainingCommunity.CommunityViewHolder> {

    private final List<HashMap<String, String>> communityList;
    private final OnCommunityActionListener listener;
    private final String currentUserRole;
    private final String currentUserUsername;
    private final Context context;

    public interface OnCommunityActionListener {
        void onJoinCommunity(int position);
        void onLeaveCommunity(int position);
    }

    public AdapterTrainingCommunity(List<HashMap<String, String>> communityList, OnCommunityActionListener listener, String currentUserRole, String currentUserUsername, Context context) {
        this.communityList = communityList;
        this.listener = listener;
        this.currentUserRole = currentUserRole;
        this.currentUserUsername = currentUserUsername;
        this.context = context;
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_training_community, parent, false);
        return new CommunityViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        HashMap<String, String> community = communityList.get(position);
        holder.tvCommunityName.setText("Community Name: " + community.get("title"));
        holder.tvTrainerName.setText("Trainer: " + community.get("trainer"));
        holder.tvCommunityDescription.setText("Trainer Recommendation: " + community.get("description"));

        // Determine if the user is a member
        boolean isMember = community.containsKey("isMember") && community.get("isMember").equals("true");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActivityCommunityDetail.class);
            intent.putExtra("communityTitle", community.get("title"));
            intent.putExtra("trainerUsername", community.get("trainer"));
            intent.putExtra("currentUserRole", currentUserRole);
            intent.putExtra("currentUserUsername", currentUserUsername);
            intent.putExtra("isMember", isMember);
            context.startActivity(intent);
        });

        if (!currentUserRole.equalsIgnoreCase("TRAINER")) {
            holder.btnJoinLeaveCommunity.setVisibility(View.VISIBLE);
            if (isMember) {
                holder.btnJoinLeaveCommunity.setText("Leave Community");
                holder.btnJoinLeaveCommunity.setOnClickListener(v -> listener.onLeaveCommunity(holder.getAdapterPosition()));
            } else {
                holder.btnJoinLeaveCommunity.setText("Join Community");
                holder.btnJoinLeaveCommunity.setOnClickListener(v -> listener.onJoinCommunity(holder.getAdapterPosition()));
            }
        } else {
            holder.btnJoinLeaveCommunity.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }

    static class CommunityViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommunityName, tvTrainerName, tvCommunityDescription;
        Button btnJoinLeaveCommunity;

        public CommunityViewHolder(@NonNull View itemView, OnCommunityActionListener listener) {
            super(itemView);
            tvCommunityName = itemView.findViewById(R.id.tvCommunityName);
            tvTrainerName = itemView.findViewById(R.id.tvTrainerName);
            tvCommunityDescription = itemView.findViewById(R.id.tvCommunityDescription);
            btnJoinLeaveCommunity = itemView.findViewById(R.id.btnJoinLeaveCommunity);
        }
    }
}
